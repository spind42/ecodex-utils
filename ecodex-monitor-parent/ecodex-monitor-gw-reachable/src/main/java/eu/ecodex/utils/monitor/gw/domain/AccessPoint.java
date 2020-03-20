package eu.ecodex.utils.monitor.gw.domain;

import lombok.Data;

@Data
public class AccessPoint {

    /**
     * Name of the endpoint
     */
    String name;

    /**
     * URL of the endpoint
     *  eg. service.example.com/domibus/services/msh
     */
    String endpoint;

}
