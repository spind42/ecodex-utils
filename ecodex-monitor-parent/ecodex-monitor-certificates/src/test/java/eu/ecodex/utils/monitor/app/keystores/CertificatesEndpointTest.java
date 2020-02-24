package eu.ecodex.utils.monitor.app.keystores;

import eu.ecodex.utils.monitor.app.keystores.report.StoreEntryInfo;
import eu.ecodex.utils.monitor.app.keystores.report.StoreInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class CertificatesEndpointTest {

    public static final String USERNAME = "admin";
    public static final String PASSWORD = "admin";

    @LocalServerPort
    int localServerPort;
    private RestTemplate restTemplate;


//    @Autowired
//    private TestRestTemplate restTemplate;

    @BeforeEach
    public void initRestTemplate() {
        RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder();
        RestTemplate build = restTemplateBuilder
                .basicAuthentication(USERNAME, PASSWORD)
                .build();

        this.restTemplate = build;
    }

    @Test
    void testReadStores() {
        HttpHeaders headers = new HttpHeaders();
        HttpEntity <String> entity = new HttpEntity<String>(headers);

        String url = "http://localhost:" + localServerPort + "/actuator/certificates";
        ResponseEntity<String> exchange = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

        assertThat(exchange.getBody()).isNotNull();

//        assertThat(forEntity).isNotNull();
//        restTemplate.
    }

    @Test
    void testReadStoreInfo() {
        HttpHeaders headers = new HttpHeaders();
        HttpEntity <String> entity = new HttpEntity<String>(headers);

        String url = "http://localhost:" + localServerPort + "/actuator/certificates/gwstore";
        ResponseEntity<StoreInfo> exchange = restTemplate.exchange(url, HttpMethod.GET, entity, StoreInfo.class);

        StoreInfo info = exchange.getBody();
        assertThat(info).isNotNull();
        assertThat(info.getName()).isEqualTo("gwstore");
        assertThat(info.getReadable()).isTrue();
    }

    @Test
    void testCertInfo() {
        HttpHeaders headers = new HttpHeaders();
        HttpEntity <String> entity = new HttpEntity<String>(headers);

        String url = "http://localhost:" + localServerPort + "/actuator/certificates/gwstore/gw2";
        ResponseEntity<StoreEntryInfo> exchange = restTemplate.exchange(url, HttpMethod.GET, entity, StoreEntryInfo.class);

        StoreEntryInfo info = exchange.getBody();
        assertThat(info).isNotNull();
        assertThat(info.getAliasName()).isEqualTo("gw2");
        assertThat(info.getCertificateType()).isEqualTo("X.509");
    }

    @Test
    void testCertInfo_notExistent() {
        HttpHeaders headers = new HttpHeaders();
        HttpEntity <String> entity = new HttpEntity<String>(headers);

        String url = "http://localhost:" + localServerPort + "/actuator/certificates/gwstore/gw123123";
        ResponseEntity<StoreEntryInfo> exchange = restTemplate.exchange(url, HttpMethod.GET, entity, StoreEntryInfo.class);

        StoreEntryInfo info = exchange.getBody();
        assertThat(info).isNotNull();
        assertThat(info.getAliasName()).isEqualTo("gw123123");
        assertThat(info.getPresent()).isFalse();

    }
}