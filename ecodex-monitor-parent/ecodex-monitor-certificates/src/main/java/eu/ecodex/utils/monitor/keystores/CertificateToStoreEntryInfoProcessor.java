package eu.ecodex.utils.monitor.keystores;

import eu.ecodex.utils.monitor.keystores.dto.StoreEntryInfo;

public interface CertificateToStoreEntryInfoProcessor {
    boolean canProcess(String crtType);

    StoreEntryInfo processCrt(StoreEntryInfo info, byte[] crt);
}
