package eu.ecodex.utils.monitor.gw.service;

import eu.ecodex.utils.monitor.app.MonitorAppConfiguration;
import eu.ecodex.utils.monitor.gw.GatewayMonitorAutoConfiguration;
import eu.ecodex.utils.monitor.gw.domain.AccessPoint;
import eu.ecodex.utils.monitor.gw.dto.AccessPointStatusDTO;
import org.apache.catalina.Server;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import test.server.ServerStarter;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.NONE,
        classes = { GatewayMonitorAutoConfiguration.class }
)
@ActiveProfiles("test")
public class GatewaysCheckerServiceITCase {

    private static final Logger LOGGER = LoggerFactory.getLogger(GatewaysCheckerServiceITCase.class);

    @Autowired
    GatewaysCheckerService gatewaysCheckerService;

    @Test
    void getGatewayStatus_serverCrtDoesNotMatchName() {

        ConfigurableApplicationContext SERVER1 = ServerStarter.startServer1();

        AccessPoint ap = new AccessPoint();
        ap.setName("gw1");
        ap.setEndpoint("https://localhost:" + ServerStarter.getServerPort(SERVER1) + "/");

        AccessPointStatusDTO gatewayStatus = gatewaysCheckerService.getGatewayStatus(ap);

        LOGGER.info("Gateway status is: [{}]", gatewayStatus);

        assertThat(gatewayStatus.getFailures()).hasSize(1);
    }

    @Test
    void getGatewayStatus2_illegalClientCrt() {

        ConfigurableApplicationContext SERVER2 = ServerStarter.startServer2();

        AccessPoint ap = new AccessPoint();
        ap.setName("gw2");
        ap.setEndpoint("https://localhost:" + ServerStarter.getServerPort(SERVER2) + "/");

        AccessPointStatusDTO gatewayStatus = gatewaysCheckerService.getGatewayStatus(ap);

        LOGGER.info("Gateway status is: [{}]", gatewayStatus);

        assertThat(gatewayStatus.getFailures()).hasSize(1);
    }

    @Test
    void getGatewayStatus3() {

        ConfigurableApplicationContext SERVER3 = ServerStarter.startServer3();

        AccessPoint ap = new AccessPoint();
        ap.setName("gw3");
        ap.setEndpoint("https://localhost:" + ServerStarter.getServerPort(SERVER3) + "/");

        AccessPointStatusDTO gatewayStatus = gatewaysCheckerService.getGatewayStatus(ap);

        LOGGER.info("Gateway status is: [{}]", gatewayStatus);

        assertThat(gatewayStatus.getFailures()).hasSize(0);
    }

    @Test
    void getGatewayStatus_recheck() throws InterruptedException {

        ConfigurableApplicationContext SERVER3 = ServerStarter.startServer3();

        AccessPoint ap = new AccessPoint();
        ap.setName("gw3");
        ap.setEndpoint("https://localhost:" + ServerStarter.getServerPort(SERVER3) + "/");

        AccessPointStatusDTO gatewayStatus = gatewaysCheckerService.getGatewayStatus(ap);
        LOGGER.info("Gateway status is: [{}]", gatewayStatus);
        assertThat(gatewayStatus.getFailures()).hasSize(0);

        LOGGER.info("sleep 8s");
        Thread.sleep(Duration.ofSeconds(8).toMillis());

        AccessPointStatusDTO gatewayStatus2 = gatewaysCheckerService.getGatewayStatus(ap);
        LOGGER.info("Gateway status is: [{}]", gatewayStatus2);
        assertThat(gatewayStatus).isNotEqualTo(gatewayStatus2);


    }

    @Test
    void getGatewayStatus4_illegalServerCrt() throws InterruptedException {

        ConfigurableApplicationContext SERVER4 = ServerStarter.startServer4();

        AccessPoint ap = new AccessPoint();
        ap.setName("gw4");
        ap.setEndpoint("https://localhost:" + ServerStarter.getServerPort(SERVER4) + "/");

        AccessPointStatusDTO gatewayStatus = gatewaysCheckerService.getGatewayStatus(ap);
        AccessPointStatusDTO gatewayStatus1 = gatewaysCheckerService.getGatewayStatus(ap);
        Thread.sleep(Duration.ofSeconds(2).toMillis());
        AccessPointStatusDTO gatewayStatus2 = gatewaysCheckerService.getGatewayStatus(ap);

        LOGGER.info("Gateway status is: [{}]", gatewayStatus);

        assertThat(gatewayStatus.getFailures()).hasSize(1);

        assertThat(gatewayStatus).isSameAs(gatewayStatus1);
        assertThat(gatewayStatus).isSameAs(gatewayStatus2);
    }



}