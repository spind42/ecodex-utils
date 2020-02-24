package eu.ecodex.utils.monitor.app.keystores;

import eu.ecodex.utils.monitor.app.keystores.report.StoreEntryInfo;
import org.bouncycastle.cert.X509CertificateHolder;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class X509CertificateToStoreEntryInfoProcessorImpl implements CertificateToStoreEntryInfoProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(X509CertificateToStoreEntryInfoProcessorImpl.class);

    @Override
    public boolean canProcess(String crtType) {
        return "X.509".equals(crtType);
    }

    @Override
    public StoreEntryInfo processCrt(StoreEntryInfo info, byte[] crt) {
        try {
            X509CertificateHolder certificateHolder = new X509CertificateHolder(crt);
            info.setVersionNumber(certificateHolder.getVersionNumber());
            info.setIssuerName(certificateHolder.getIssuer().toString());
            info.setSubject(certificateHolder.getSubject().toString());
            info.setSerialNumber(certificateHolder.getSerialNumber());
            info.setNotAfter(certificateHolder.getNotAfter());
            info.setNotBefore(certificateHolder.getNotBefore());


        } catch (IOException e) {
            LOGGER.warn("Error while reading certificate", e);
        }


        return info;
    }

}

