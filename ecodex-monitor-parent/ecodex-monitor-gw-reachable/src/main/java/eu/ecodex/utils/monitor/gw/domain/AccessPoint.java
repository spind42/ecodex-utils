package eu.ecodex.utils.monitor.gw.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class AccessPoint {

    /**
     * Name of the endpoint
     */
    @EqualsAndHashCode.Include
    String name;

    /**
     * URL of the endpoint
     *  eg. service.example.com/domibus/services/msh
     */
    String endpoint;

}
