package eu.ecodex.utils.monitor.gw.service;

import eu.ecodex.configuration.pmode.Configuration;
import eu.ecodex.utils.monitor.gw.config.GatewayRestInterfaceConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@Disabled("tests needs external resources")
public class PModeDownloaderTest {

    PModeDownloader pModeDownloader;

    @BeforeEach
    public void beforeEach() {
        GatewayRestInterfaceConfiguration gwConfig = new GatewayRestInterfaceConfiguration();
        gwConfig.setUsername("admin");
        gwConfig.setUrl("http://localhost:8020/domibus");
        gwConfig.setPassword("123456");

        pModeDownloader = new PModeDownloader(gwConfig);
    }

    @Test
    void downloadPModes() {
        Configuration configuration = pModeDownloader.downloadPModes();
        assertThat(configuration).isNotNull();
    }

//    @Test
//    void authenticate() {
//    }

}