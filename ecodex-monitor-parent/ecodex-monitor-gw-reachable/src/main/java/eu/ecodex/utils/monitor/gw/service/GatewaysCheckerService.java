package eu.ecodex.utils.monitor.gw.service;

import eu.ecodex.utils.monitor.gw.config.GatewayMonitorConfigurationProperties;
import eu.ecodex.utils.monitor.gw.domain.AccessPoint;
import eu.ecodex.utils.monitor.gw.dto.AccessPointStatusDTO;
import eu.ecodex.utils.monitor.gw.dto.CheckResultDTO;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.io.HttpClientConnectionManager;
import org.apache.hc.client5.http.protocol.HttpClientContext;
import org.apache.hc.client5.http.ssl.DefaultHostnameVerifier;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactoryBuilder;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.ProtocolVersion;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.ssl.TLS;
import org.apache.hc.core5.ssl.PrivateKeyDetails;
import org.apache.hc.core5.ssl.PrivateKeyStrategy;
import org.apache.hc.core5.ssl.SSLContexts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Base64Utils;
import org.springframework.util.CollectionUtils;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLSession;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class GatewaysCheckerService {

    private static final Logger LOGGER = LoggerFactory.getLogger(GatewaysCheckerService.class);

    @Autowired
    GatewayMonitorConfigurationProperties gatewayMonitorConfig;

    @Autowired
    TrustStoreCompleteChainTrustStrategy trustStoreCompleteChainTrustStrategy;

    private Map<AccessPoint, AccessPointStatusDTO> apCheck = new HashMap<>();

    public AccessPointStatusDTO getGatewayStatus(AccessPoint ap) {
        return getGatewayStatus(ap, gatewayMonitorConfig.getCheckCacheTimeout());
    }

    public AccessPointStatusDTO getGatewayStatus(AccessPoint ap, Duration cacheTimeout) {
        AccessPointStatusDTO status = apCheck.get(ap);
        if (status != null && status.getCheckTime().plus(cacheTimeout).isAfter(ZonedDateTime.now()) ) {
            LOGGER.trace("Checking [{}] and hitting [{}] + [{}] cache last check was on [{}]", ap, ZonedDateTime.now(), cacheTimeout, status.getCheckTime());
            return status;
        }
        LOGGER.info("Checking endpoint [{}]", ap);
        status = new AccessPointStatusDTO();
        status.setCheckTime(ZonedDateTime.now());
        status.setEndpoint(ap.getEndpoint());
        status.setName(ap.getName());
        apCheck.put(ap, status);



        char[] privateKeyPassword = gatewayMonitorConfig.getTls().getPrivateKey().getPassword().toCharArray();
        KeyStore keyStore = gatewayMonitorConfig.getTls().getKeyStore().loadKeyStore();
        KeyStore trustStore = gatewayMonitorConfig.getTls().getTrustStore().loadKeyStore();

        String minTlsString = gatewayMonitorConfig.getTls().getMinTls();



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

        } catch (NoSuchAlgorithmException | KeyManagementException | KeyStoreException | UnrecoverableKeyException e) {
            LOGGER.error("Error while setting up SSLContext", e);
            CheckResultDTO c = new CheckResultDTO();
            c.setName("SSLContext setup");
            c.setMessage(e.getMessage());
            c.writeStackTraceIntoDetails(e);
        }

        LOGGER.trace("Client supports: [{}]", CollectionUtils.arrayToList(sslcontext.getSupportedSSLParameters().getProtocols()));

        final ProtocolVersion[] allowedTls;
        ProtocolVersion[] supportedClientProtos =
                Stream.of(sslcontext.getSupportedSSLParameters().getProtocols())
                .map(s -> {
                    try {
                        return TLS.parse(s);
                    } catch (ParseException e) {
                        return null;
                    }
                })
                .filter(p -> p != null)
                .toArray(ProtocolVersion[]::new);
        LOGGER.debug("Supported and Allowed client protocols are [{}]", CollectionUtils.arrayToList(supportedClientProtos));

        ProtocolVersion minTls;
        try {
            minTls = TLS.parse(minTlsString);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        allowedTls = Stream.of(TLS.values())
                    .filter(tls -> tls.greaterEquals(minTls))
                    .map(t -> { return t.version;})
                    .filter(p -> ArrayUtils.contains(supportedClientProtos, p))
                    .toArray(ProtocolVersion[]::new);
        status.setAllowedTls(allowedTls);


        LOGGER.trace("allowed TLS protocols are [{}]", CollectionUtils.arrayToList(status.getAllowedTls()));

        if (allowedTls.length == 0) {
            CheckResultDTO f = new CheckResultDTO();
            f.setMessage("Client does not support minTls!");
            status.getFailures().add(f);
            LOGGER.warn("Client supports TLS portocols [{}] but required minTls [{}] is not part of it!", CollectionUtils.arrayToList(supportedClientProtos), minTls);
        }

        DefaultHostnameVerifier defaultHostnameVerifier = new DefaultHostnameVerifier();

        TLS[] tls = Stream.of(allowedTls)
                .map(this::mapProtocolVersionToTLS)
                .toArray(TLS[]::new);

        final SSLConnectionSocketFactory sslSocketFactory = SSLConnectionSocketFactoryBuilder.create()
                .setSslContext(sslcontext)
                .setTlsVersions(tls)
                .setHostnameVerifier(defaultHostnameVerifier)
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


                if (response.getCode() != 200) {
                    CheckResultDTO c = new CheckResultDTO();
                    c.setName("HTTP Code");
                    c.setMessage("HTTP Code != 200");
                    status.getFailures().add(c);
                }
            } catch (SSLHandshakeException sslHandshakeException) {
                LOGGER.error("TLS Handshake failed due", sslHandshakeException);

                CheckResultDTO f = new CheckResultDTO();
                f.setName("TLS failure");
                f.setMessage("TLS Handshake failed!");

                //TODO: switch for print stack trace...
                f.writeStackTraceIntoDetails(sslHandshakeException);
                status.getFailures().add(f);
            } finally {
                final SSLSession sslSession = clientContext.getSSLSession();
                if (sslSession != null) {
                    LOGGER.debug("TLS protocol {}", sslSession.getProtocol());
                    LOGGER.debug("TLS cipher suite {}", sslSession.getCipherSuite());

                    status.setUsedTls(TLS.parse(sslSession.getProtocol()));
                    status.setLocalCertificates(convertToBase64StringArray(sslSession.getLocalCertificates()));
                    status.setServerCertificates(convertToBase64StringArray(sslSession.getPeerCertificates()));
                } else {
                    LOGGER.info("SSL session is null, cannot provide any information!");
                }
                status.setProxyHost(clientContext.getHttpRoute().getProxyHost());
                status.setTargetHost(clientContext.getHttpRoute().getTargetHost());
            }
        } catch (IOException | ParseException | URISyntaxException | IllegalArgumentException e) {
            CheckResultDTO f = new CheckResultDTO();
            f.setName("Connection Failure");
            f.setMessage("Connection failed");
            f.writeStackTraceIntoDetails(e);
            status.getFailures().add(f);
        }
        return status;
    }

    private TLS mapProtocolVersionToTLS(ProtocolVersion protocolVersion) {
        return Stream.of(TLS.values())
                .filter(t -> t.isSame(protocolVersion))
                .findFirst()
                .get();
    }

    private String[] convertToBase64StringArray(java.security.cert.Certificate[] certificates) {
        if (certificates == null) {
            return null;
        }
        return Stream.of(certificates)
                .map(this::mapToBase64String)
                .toArray(String[]::new);
    }

    private String mapToBase64String(Certificate certificate) {
        try {
            byte[] encoded = certificate.getEncoded();
            String s = Base64Utils.encodeToString(encoded);
            return s;
        } catch (CertificateEncodingException e) {
            throw new RuntimeException(e);
        }
    }


}

