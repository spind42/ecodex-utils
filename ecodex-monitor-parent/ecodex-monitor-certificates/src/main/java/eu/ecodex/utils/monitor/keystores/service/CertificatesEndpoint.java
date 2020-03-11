package eu.ecodex.utils.monitor.keystores.service;


import eu.ecodex.utils.monitor.keystores.ConditionalOnCertificatesCheckEnabled;
import eu.ecodex.utils.monitor.keystores.dto.StoreEntryInfo;
import eu.ecodex.utils.monitor.keystores.dto.StoreInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.endpoint.annotation.Selector;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;

import java.util.*;

@Endpoint(id = "certificates")
public class CertificatesEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(CertificatesEndpoint.class);

    @Autowired
    KeyService keyService;

    @ReadOperation
    public Map<String, StoreInfo> getStores() {
        return keyService.getStores();
    }


    @ReadOperation
    public StoreInfo getStoreEntryInfo(@Selector String storeName) {
        return keyService.getStoreEntryInfo(storeName);
    }

    @ReadOperation
    public StoreEntryInfo getStoreEntryInfo(@Selector String storeName, @Selector String aliasName) {
        return keyService.getStoreEntryInfo(null, storeName, aliasName);
    }


}
