package eu.ecodex.utils.monitor.gw.config;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class HealthCheckProperties {

    /**
     * should we check ourself,
     * by default true
     */
    boolean checkSelf = true;
    /**
     * which remote gateways should be checked
     * by health check?
     *  a * means all
     */
    List<String> checkNames = new ArrayList<>();

}
