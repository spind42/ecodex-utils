package eu.ecodex.utils.monitor.app.activemq;

import eu.ecodex.utils.monitor.app.activemq.dto.QueueInfo;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.test.context.ActiveProfiles;

import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@EnableJms
class ActiveMqQueuesMonitorEndpointTest {

    @LocalServerPort
    int localServerPort;

    @Autowired
    TestRestTemplate restTemplate;

//    @Before
//    public void beforeAll() {
//        restTemplate = TestRestTemplate();
//    }

    @Test
    void getQueues() {
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> entity = new HttpEntity<String>(headers);

        String url = "http://localhost:" + localServerPort + "/actuator/activemq/queues";
        ResponseEntity<Collection> exchange = restTemplate.exchange(url, HttpMethod.GET, entity, Collection.class);




    }
}