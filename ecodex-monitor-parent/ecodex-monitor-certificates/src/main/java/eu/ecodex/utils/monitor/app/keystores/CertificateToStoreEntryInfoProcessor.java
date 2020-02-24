package eu.ecodex.utils.monitor.app.keystores;

import eu.ecodex.utils.monitor.app.keystores.report.StoreEntryInfo;

public interface CertificateToStoreEntryInfoProcessor {
    boolean canProcess(String crtType);

    StoreEntryInfo processCrt(StoreEntryInfo info, byte[] crt);
}
