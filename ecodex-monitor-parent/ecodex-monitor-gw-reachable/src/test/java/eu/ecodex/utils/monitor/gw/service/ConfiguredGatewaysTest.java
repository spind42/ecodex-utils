package eu.ecodex.utils.monitor.gw.service;

import eu.ecodex.utils.monitor.gw.config.GatewayMonitorConfigurationProperties;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;

public class ConfiguredGatewaysTest {

    ConfiguredGatewaysService configuredGateways;

    @BeforeEach
    public void beforeEach() {
        configuredGateways = new ConfiguredGatewaysService();


    }



    @Test
    public void testInit() {
        configuredGateways.init();
    }

}
