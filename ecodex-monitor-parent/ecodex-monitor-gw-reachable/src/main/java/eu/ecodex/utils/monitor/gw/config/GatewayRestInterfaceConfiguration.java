package eu.ecodex.utils.monitor.gw.config;

import lombok.Data;

@Data
public class GatewayRestInterfaceConfiguration {

    boolean loadPmodes = true;

    /**
     * The URL of the gateway
     */
    private String url;

    /**
     * A gateway user which has the rights to at least
     * download the current p-mode set
     */
    private String username;

    /**
     * The password for the gateway user
     */
    private String password;

}
