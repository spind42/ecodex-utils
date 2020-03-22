package eu.ecodex.utils.monitor.gw.service;

import eu.ecodex.utils.monitor.app.MonitorAppConfiguration;
import eu.ecodex.utils.monitor.gw.GatewayMonitorAutoConfiguration;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.client.RootUriTemplateHandler;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = { GatewayMonitorAutoConfiguration.class, MonitorAppConfiguration.class }
        )
@ActiveProfiles("test")
@Disabled("test needs external resource!")
public class GatewayHealthIndicatorTest {

    @Autowired
    GatewayHealthIndicator gatewayHealthIndicator;

    @LocalServerPort
    int localPort;


    @Test
    public void testGateway() {
        RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder();
        RestTemplate build = restTemplateBuilder
                .uriTemplateHandler(new RootUriTemplateHandler("http://localhost:" + localPort + "/actuator/health"))
                .basicAuthentication("test", "test")
                .build();

        ResponseEntity<String> forEntity = build.getForEntity("/", String.class);

        assertThat(forEntity).isNotNull();

        System.out.println(forEntity.getBody());
    }



}