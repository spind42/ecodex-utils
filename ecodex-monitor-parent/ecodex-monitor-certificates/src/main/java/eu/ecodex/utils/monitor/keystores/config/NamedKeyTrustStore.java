package eu.ecodex.utils.monitor.keystores.config;

import eu.domibus.connector.lib.spring.configuration.StoreConfigurationProperties;
import lombok.Data;

@Data
public class NamedKeyTrustStore extends StoreConfigurationProperties {

    /**
     * name of the key or truststore
     * is used by the checks to reference this store
     */
    String name;

    /**
     * Which store information should be exposed
     * set to null or empty String if none
     */
    String metricExposed = "*";

    /**
     * Which entry should be exposed?
     *
     * use comma seperated lists or expression here...
     */
    String entryExposed = "*";


    /**
     * Which information about the entry should be exposed?
     */
    String entryMetricExposed = "*";


}
