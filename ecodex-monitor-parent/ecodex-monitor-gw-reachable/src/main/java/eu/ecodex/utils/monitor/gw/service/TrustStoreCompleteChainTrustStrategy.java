package eu.ecodex.utils.monitor.gw.service;

import eu.domibus.connector.lib.spring.configuration.StoreConfigurationProperties;
import eu.ecodex.utils.monitor.gw.config.GatewayMonitorConfigurationProperties;
import org.apache.hc.core5.ssl.TrustStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.*;
import java.util.*;

@Component
public class TrustStoreCompleteChainTrustStrategy implements TrustStrategy {

    private static final Logger LOGGER = LoggerFactory.getLogger(TrustStoreCompleteChainTrustStrategy.class);

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
        validateCertificate(firstCertificate);
        return false;
    }

    private void validateCertificate(X509Certificate crt) throws CertificateException {
        try {
            validateKeyChain(crt, trustStore);
        } catch (KeyStoreException | InvalidAlgorithmParameterException | NoSuchAlgorithmException | NoSuchProviderException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Validate keychain
     * @param client is the client X509Certificate
     * @param keyStore containing all trusted certificate
     * @return true if validation until root certificate success, false otherwise
     * @throws KeyStoreException if the provided key store cannot be open
     * @throws CertificateException {@link #validateKeyChain(X509Certificate, X509Certificate...)}
     * @throws InvalidAlgorithmParameterException {@link #validateKeyChain(X509Certificate, X509Certificate...)}
     * @throws NoSuchAlgorithmException {@link #validateKeyChain(X509Certificate, X509Certificate...)}
     * @throws NoSuchProviderException {@link #validateKeyChain(X509Certificate, X509Certificate...)}
     */
    public boolean validateKeyChain(X509Certificate client,
                                           KeyStore keyStore) throws KeyStoreException, CertificateException,
            InvalidAlgorithmParameterException, NoSuchAlgorithmException,
            NoSuchProviderException {
        X509Certificate[] certs = new X509Certificate[keyStore.size()];
        int i = 0;
        Enumeration<String> alias = keyStore.aliases();

        while (alias.hasMoreElements()) {
            certs[i++] = (X509Certificate) keyStore.getCertificate(alias
                    .nextElement());
        }

        return validateKeyChain(client, certs);
    }

    /**
     * Validate keychain
     * @param client is the client X509Certificate
     * @param trustedCerts is Array containing all trusted X509Certificate
     * @return true if validation until root certificate success, false otherwise
     * @throws CertificateException thrown if the certificate is invalid
     * @throws InvalidAlgorithmParameterException @see {@link CertPathValidator#validate(CertPath, CertPathParameters)}
     * @throws NoSuchAlgorithmException @see {@link CertPathValidator#validate(CertPath, CertPathParameters)}
     * @throws NoSuchProviderException @see {@link CertPathValidator#validate(CertPath, CertPathParameters)}
     */
    public boolean validateKeyChain(X509Certificate client, X509Certificate... trustedCerts) throws CertificateException,
            InvalidAlgorithmParameterException, NoSuchAlgorithmException,
            NoSuchProviderException {
        boolean found = false;
        int i = trustedCerts.length;
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        TrustAnchor anchor;
        Set anchors;
        CertPath path;
        List list;
        PKIXParameters params;
        CertPathValidator validator = CertPathValidator.getInstance("PKIX");

        while (!found && i > 0) {
            anchor = new TrustAnchor(trustedCerts[--i], null);
            anchors = Collections.singleton(anchor);

            list = Arrays.asList(new Certificate[] { client });
            path = cf.generateCertPath(list);

            params = new PKIXParameters(anchors);
            params.setRevocationEnabled(false); //TODO: add config for revocation checks!

            X509Certificate currentCert = trustedCerts[i];
            if (client.getIssuerDN().equals(currentCert.getSubjectDN())) {
                try {
                    validator.validate(path, params);
                    if (isSelfSigned(currentCert)) {
//                        LOGGER.debug("found root CA [{}]", currentCert.getSubjectDN());
                        found = true;
                        LOGGER.debug("validating root [{}]", currentCert.getSubjectX500Principal().getName());
                    } else if (!client.equals(currentCert)) {
                        // find parent ca
                        LOGGER.debug("validating [{}] via: [{}] ", client.getSubjectX500Principal().getName(), currentCert.getSubjectX500Principal().getName());
                        found = validateKeyChain(currentCert, trustedCerts);
                    }
                } catch (CertPathValidatorException e) {
                    LOGGER.trace("validation fail, check next certifiacet in the trustedCerts array");
                }
            }
        }
        return found;
    }

    /**
     *
     * @param cert is X509Certificate that will be tested
     * @return true if cert is self signed, false otherwise
     * @throws CertificateException if the certificate is invalid
     * @throws NoSuchAlgorithmException @see {@link X509Certificate#verify(PublicKey)}
     * @throws NoSuchProviderException @see {@link X509Certificate#verify(PublicKey)}
     */
    public boolean isSelfSigned(X509Certificate cert)
            throws CertificateException, NoSuchAlgorithmException,
            NoSuchProviderException {
        try {
            PublicKey key = cert.getPublicKey();

            cert.verify(key);
            return true;
        } catch (SignatureException sigEx) {
            return false;
        } catch (InvalidKeyException keyEx) {
            return false;
        }
    }
}

