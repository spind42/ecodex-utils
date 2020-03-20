package eu.ecodex.utils.monitor.gw.service;

import eu.domibus.connector.lib.spring.configuration.StoreConfigurationProperties;
import eu.ecodex.utils.monitor.gw.config.GatewayMonitorConfigurationProperties;
import org.apache.commons.lang3.StringUtils;
import org.apache.hc.core5.ssl.TrustStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;
import java.util.Date;

@Component
public class TrustStoreCompleteChainTrustStrategy implements TrustStrategy {

    @Autowired
    GatewayMonitorConfigurationProperties gatewayMonitorConfigurationProperties;

    private KeyStore trustStore;
    private StoreConfigurationProperties trustStoreConfig;

    @PostConstruct
    public void init() {
        this.trustStore = gatewayMonitorConfigurationProperties.getTls().getTrustStore().loadKeyStore();
        this.trustStoreConfig = gatewayMonitorConfigurationProperties.getTls().getTrustStore();

    }

    @Override
    public boolean isTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

        X509Certificate firstCertificate = x509Certificates[0];

//        firstCertificate.checkValidity(new Date());

        if (isInStore(firstCertificate)) {
            new CertificateException("Certificate not part of trusted certificates within TrustStore " + trustStoreConfig.getPathUrlAsString());
        }


//        checkCertificate(X509Certificate[] x509Certificates)

        return false;
    }

    private boolean isInStore(X509Certificate certificate) {
        try {
            String certificateAlias = trustStore.getCertificateAlias(certificate);
            return StringUtils.isNotEmpty(certificateAlias);
        } catch (KeyStoreException kse) {
            throw new RuntimeException(kse);
        }
    }

}
