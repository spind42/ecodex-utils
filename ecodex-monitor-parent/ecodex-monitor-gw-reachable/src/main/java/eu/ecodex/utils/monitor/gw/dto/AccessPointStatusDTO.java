package eu.ecodex.utils.monitor.gw.dto;

import lombok.Data;
import lombok.ToString;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.ProtocolVersion;
import org.apache.hc.core5.http.ssl.TLS;

import java.security.cert.Certificate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@ToString
public class AccessPointStatusDTO {

    /**
     * Name of the endpoint
     */
    String name;

    /**
     * URL of the endpoint
     *  eg. service.example.com/domibus/services/msh
     */
    String endpoint;

    ProtocolVersion[] allowedTls;

    ProtocolVersion usedTls;

    String[] localCertificates;

    String[] serverCertificates;

    List<CheckResultDTO> failures = new ArrayList<>();

    List<CheckResultDTO> warnings = new ArrayList<>();

    ZonedDateTime checkTime;

    HttpHost proxyHost;

    HttpHost targetHost;

}
