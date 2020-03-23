package eu.ecodex.utils.monitor.gw;


import eu.ecodex.utils.monitor.gw.config.GatewayMonitorConfigurationProperties;
import eu.ecodex.utils.monitor.gw.config.GatewayRestInterfaceConfiguration;
import eu.ecodex.utils.monitor.gw.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ConditionalOnProperty(prefix = GatewayMonitorConfigurationProperties.GATEWAY_MONITOR_PREFIX, name = "enabled", havingValue = "true")
@EnableConfigurationProperties(GatewayMonitorConfigurationProperties.class)
@Import(ServiceConfiguration.class)
public class GatewayMonitorAutoConfiguration {

    @Autowired
    private GatewayMonitorConfigurationProperties gatewayRestInterfaceConfiguration;

    @Bean
    public PModeDownloader pModeDownloader() {
        return new PModeDownloader(gatewayRestInterfaceConfiguration.getRest());
    }

    @Bean
    public ConfiguredGatewaysService configuredGatewaysService() {
        return new ConfiguredGatewaysService();
    }

    @Bean
    public GatewayHealthIndicator gatewayHealthIndicator() {
        return new GatewayHealthIndicator();
    }

    @Bean
    public GatewayReachableEndpoint gatewayReachableEndpoint() {
        return new GatewayReachableEndpoint();
    }

}
