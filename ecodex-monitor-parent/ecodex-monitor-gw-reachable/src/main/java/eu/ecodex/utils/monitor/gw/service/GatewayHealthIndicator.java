package eu.ecodex.utils.monitor.gw.service;

import eu.ecodex.utils.monitor.gw.config.GatewayMonitorConfigurationProperties;
import eu.ecodex.utils.monitor.gw.domain.AccessPoint;
import org.apache.hc.client5.http.auth.CredentialsProvider;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.io.HttpClientConnectionManager;
import org.apache.hc.client5.http.protocol.HttpClientContext;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactoryBuilder;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.ssl.TLS;
import org.apache.hc.core5.ssl.PrivateKeyDetails;
import org.apache.hc.core5.ssl.PrivateKeyStrategy;
import org.apache.hc.core5.ssl.SSLContexts;
import org.apache.hc.core5.ssl.TrustStrategy;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

import org.apache.hc.client5.http.auth.AuthScope;
import org.apache.hc.client5.http.auth.UsernamePasswordCredentials;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.auth.BasicCredentialsProvider;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.EntityUtils;

import javax.net.ssl.*;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map;


public class GatewayHealthIndicator extends AbstractHealthIndicator {

    private static final Logger LOGGER = LoggerFactory.getLogger(GatewayHealthIndicator.class);

    @Autowired
    ConfiguredGatewaysService configuredGatewaysService;

    @Autowired
    GatewayMonitorConfigurationProperties gatewayMonitorConfig;

    @Autowired
    TrustStoreCompleteChainTrustStrategy trustStoreCompleteChainTrustStrategy;

    @Override
    protected void doHealthCheck(Health.Builder builder) throws Exception {
        builder.up();
        checkSelf(builder);

    }

    private void checkSelf(Health.Builder builder) {
        AccessPoint ap = configuredGatewaysService.getSelf();
        checkAccessPoint(builder, ap);
    }

    private void checkAccessPoint(Health.Builder builder, AccessPoint ap) {
        LOGGER.info("Checking endpoint [{}]", ap);

        char[] privateKeyPassword = gatewayMonitorConfig.getTls().getPrivateKey().getPassword().toCharArray();
        KeyStore keyStore = gatewayMonitorConfig.getTls().getKeyStore().loadKeyStore();
        KeyStore trustStore = gatewayMonitorConfig.getTls().getTrustStore().loadKeyStore();

        String minTls = gatewayMonitorConfig.getTls().getMinTls();


        SSLContext sslcontext = null;
        try {
            sslcontext = SSLContexts.custom()
                    .loadTrustMaterial(trustStore, trustStoreCompleteChainTrustStrategy)
                    .loadKeyMaterial(keyStore, privateKeyPassword, new PrivateKeyStrategy() {
                        @Override
                        public String chooseAlias(Map<String, PrivateKeyDetails> aliases, SSLParameters sslParameters) {
                            return gatewayMonitorConfig.getTls().getPrivateKey().getAlias();
                        }
                    })
                    .build();

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (KeyManagementException e) {
            throw new RuntimeException(e);
        } catch (KeyStoreException e) {
            throw new RuntimeException(e);
        } catch (UnrecoverableKeyException e) {
            throw new RuntimeException(e);
        }
        // Allow TLSv1.2 protocol only
        final SSLConnectionSocketFactory sslSocketFactory = SSLConnectionSocketFactoryBuilder.create()
                .setSslContext(sslcontext)
                .setTlsVersions(minTls)
                .build();

        final HttpClientConnectionManager cm = PoolingHttpClientConnectionManagerBuilder.create()
                .setSSLSocketFactory(sslSocketFactory)
                .build();
        try (CloseableHttpClient httpclient = HttpClients.custom()
                .setConnectionManager(cm)
                .build()) {
            final HttpGet httpRequest = new HttpGet(ap.getEndpoint());

            LOGGER.debug("Executing request " + httpRequest.getMethod() + " " + httpRequest.getUri());

            final HttpClientContext clientContext = HttpClientContext.create();
            try (CloseableHttpResponse response = httpclient.execute(httpRequest, clientContext)) {
                LOGGER.debug("----------------------------------------");
                LOGGER.debug(response.getCode() + " " + response.getReasonPhrase());
                LOGGER.debug(EntityUtils.toString(response.getEntity()));

                final SSLSession sslSession = clientContext.getSSLSession();
                if (sslSession != null) {
                    LOGGER.debug("SSL protocol " + sslSession.getProtocol());
                    LOGGER.debug("SSL cipher suite " + sslSession.getCipherSuite());
                }
            }
        } catch (IOException | ParseException | URISyntaxException e) {
            throw new RuntimeException(e);
        }

    }

}
