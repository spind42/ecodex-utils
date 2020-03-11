package eu.ecodex.utils.monitor.keystores.service;

import eu.domibus.connector.lib.spring.configuration.StoreConfigurationProperties;
import eu.ecodex.utils.monitor.keystores.CertificateToStoreEntryInfoProcessor;
import eu.ecodex.utils.monitor.keystores.ConditionalOnCertificatesCheckEnabled;
import eu.ecodex.utils.monitor.keystores.config.CertificateConfigurationProperties;
import eu.ecodex.utils.monitor.keystores.config.NamedKeyTrustStore;
import eu.ecodex.utils.monitor.keystores.dto.StoreEntryInfo;
import eu.ecodex.utils.monitor.keystores.dto.StoreInfo;
import io.micrometer.core.instrument.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.annotation.Selector;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotBlank;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.nio.file.Path;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.util.*;
import java.util.stream.Collectors;

public class KeyService {

    private static final Logger LOGGER = LoggerFactory.getLogger(KeyService.class);

    @Autowired
    CertificateConfigurationProperties crtCheckConfig;

    @Autowired(required = false)
    List<CertificateToStoreEntryInfoProcessor> crtProcessorList;

    public Map<String, StoreInfo> getStores() {
        return crtCheckConfig.getStores()
                .stream()
                .map(this::mapStore)
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(s -> s.getName(), s -> s));
    }


    public StoreInfo getStoreEntryInfo(@Selector String storeName) {
        return crtCheckConfig.getStores()
                .stream()
                .filter(s -> storeName.equals(s.getName()))
                .map(this::mapStore)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(new StoreInfo());
    }


    public StoreEntryInfo getStoreEntryInfo(String exposedMetrics, String storeName, String aliasName) {
        Optional<NamedKeyTrustStore> foundKeyTrustStore = crtCheckConfig.getStores()
                .stream()
                .filter(s -> storeName.equals(s.getName()))
                .findFirst();

        if (foundKeyTrustStore.isPresent()) {
            NamedKeyTrustStore namedKeyTrustStore = foundKeyTrustStore.get();
            KeyStore keyStore = namedKeyTrustStore.loadKeyStore();
            if (exposedMetrics == null) {
                exposedMetrics = namedKeyTrustStore.getEntryMetricExposed();
            }
            return this.processKeyStoreAlias(aliasName, exposedMetrics, keyStore);
        }
        return new StoreEntryInfo();
    }

    private StoreInfo mapStore(NamedKeyTrustStore namedKeyTrustStore) {
        String exposed = namedKeyTrustStore.getMetricExposed();
        if (!StringUtils.isEmpty(exposed)) {
            StoreInfo storeInfo = new StoreInfo();
            boolean storeReadable = false;
            storeInfo.setName(namedKeyTrustStore.getName());
            List<String> split = Arrays.asList(exposed.split(","));

            if (listContainsOrWildcard(split, "configuredLocation")) {
                storeInfo.setConfiguredLocation(namedKeyTrustStore.getPath().getDescription());
            }

            if (listContainsOrWildcard(split, "location")) {

                Path path = null;
                try {
                    path = namedKeyTrustStore.getPath().getFile().toPath().toAbsolutePath();
                    storeInfo.setLocation(path.toString());
                } catch (IOException e) {
                    LOGGER.warn(String.format("IOException occured while getting path of store [%s]", namedKeyTrustStore), e);
                    storeInfo.setLocation(namedKeyTrustStore.getPathUrlAsString());
                }
            }

            try {
                namedKeyTrustStore.validatePathReadable();
                storeReadable = true;
            } catch (StoreConfigurationProperties.ValidationException ve) {
                LOGGER.warn(String.format("Store [%s] not readable due", namedKeyTrustStore), ve);
            }


            if (listContainsOrWildcard(split,"access")) {
                storeInfo.setReadable(storeReadable);
                storeInfo.setWriteable(false);

                try {
                    namedKeyTrustStore.validatePathWriteable();
                    storeInfo.setWriteable(true);
                } catch (StoreConfigurationProperties.ValidationException ve) {
                    LOGGER.debug(String.format("Store [%s] not writeable due", namedKeyTrustStore), ve);
                }
            }

            if (listContainsOrWildcard(split, "type")) {
                storeInfo.setType(namedKeyTrustStore.getType());
            }

            if (StringUtils.isNotEmpty(namedKeyTrustStore.getEntryExposed()) && storeReadable) {
                storeInfo.setStoreEntries(mapStoreEntries(namedKeyTrustStore));
            }

            //expose entries...
            return storeInfo;

        }
        return null;
    }

    private List<StoreEntryInfo> mapStoreEntries(NamedKeyTrustStore namedKeyTrustStore) {
        try {
            return processKeyStoreAliases(namedKeyTrustStore);
        } catch (StoreConfigurationProperties.CannotLoadKeyStoreException cannotLoadKeyStore) {
            LOGGER.warn("Error while loading key store!", cannotLoadKeyStore);
            return new ArrayList<>();
        } catch (KeyStoreException e) {
            LOGGER.error("Key store exception", e);
            return new ArrayList<>();
        }
    }

    private List<StoreEntryInfo> processKeyStoreAliases(@NotBlank NamedKeyTrustStore namedKeyTrustStore) throws KeyStoreException {
        List<StoreEntryInfo> entries = new ArrayList<>();
        KeyStore keyStore = namedKeyTrustStore.loadKeyStore();
        Enumeration<String> aliases = keyStore.aliases();
        List<String> exposedAliases = Arrays.asList(namedKeyTrustStore.getEntryExposed().split(","));


        while (aliases.hasMoreElements()) {
            String alias = aliases.nextElement();

            if (exposedAliases.contains("*") || exposedAliases.contains(alias)) {
                StoreEntryInfo entry = processKeyStoreAlias(alias, namedKeyTrustStore.getEntryMetricExposed(), keyStore);
                entries.add(entry);
            }
        }
        return entries;
    }

    private StoreEntryInfo processKeyStoreAlias(String alias, String entryMetricExposed, KeyStore keyStore) {
        StoreEntryInfo entry = new StoreEntryInfo();
        entry.setAliasName(alias);
        try {

            Certificate certificate = keyStore.getCertificate(alias);

            if (certificate == null) {
                entry.setPresent(false);
                return entry;
            }
            entry.setPresent(true);

            entry.setCertificateType(certificate.getType());
            Optional<CertificateToStoreEntryInfoProcessor> crtProcessor = crtProcessorList.stream()
                    .filter(p -> p.canProcess(certificate.getType()))
                    .findAny();



            if (crtProcessor.isPresent()) {
                StoreEntryInfo source;
                source = crtProcessor.get().processCrt(entry, certificate.getEncoded());
                source.setCertificate(certificate.getEncoded());

                entry = new StoreEntryInfo();
                filterMetricExposed(entryMetricExposed, source, entry);
                return entry;
            }

        } catch (KeyStoreException | CertificateEncodingException e) {
            LOGGER.warn(String.format("Failed to retrieve information from alias [%s] from keyStore [%s]", alias, keyStore), e);
        }
        return entry;
    }

    private void filterMetricExposed(String entryMetricExposed, Object source, Object target) {
        List<String> exposed = Arrays.asList(entryMetricExposed.split(","));
        List<String> ignoredCopyProperties = new ArrayList<>();

        PropertyDescriptor[] propertyDescriptors = BeanUtils.getPropertyDescriptors(source.getClass());
        for (int i = 0; i < propertyDescriptors.length; i++) {
            PropertyDescriptor prop = propertyDescriptors[i];
            if (! (exposed.contains("*") || exposed.contains(prop.getName()))) {
                ignoredCopyProperties.add(prop.getName());
            }
        }
        BeanUtils.copyProperties(source, target, ignoredCopyProperties.toArray(new String[ignoredCopyProperties.size()]));
    }

    private boolean listContainsOrWildcard(List<String> list, String contains) {
        return list.contains("*") || list.contains(contains);
    }


}
