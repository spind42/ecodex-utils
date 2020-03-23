package eu.ecodex.utils.monitor.gw.config;

import eu.domibus.connector.lib.spring.configuration.KeyAndKeyStoreAndTrustStoreConfigurationProperties;
import eu.domibus.connector.lib.spring.configuration.TLSConnectionProperties;
import eu.ecodex.utils.monitor.gw.domain.AccessPointsConfiguration;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = GatewayMonitorConfigurationProperties.GATEWAY_MONITOR_PREFIX)
@Data
public class GatewayMonitorConfigurationProperties {

    public static final String GATEWAY_MONITOR_PREFIX = "monitor.gw";


    /**
     * Configure how the Gateway config can be
     * reached and accessed
     */
    private GatewayRestInterfaceConfiguration rest;

    private AccessPointsConfiguration accessPoints;

    /**
     * This config holds the configuration
     * for TLS
     *  - TLS client authentication, keystore + private key for authentication
     *  - allowed/trusted TLS-servers
     *
     */
    private TLSConnectionProperties tls = new TLSConnectionProperties();

    private HealthCheckProperties healthCheck = new HealthCheckProperties();

    /**
     * How long should the last check result be cached?
     */
    private Duration checkCacheTimeout = Duration.ofMinutes(5);

}
