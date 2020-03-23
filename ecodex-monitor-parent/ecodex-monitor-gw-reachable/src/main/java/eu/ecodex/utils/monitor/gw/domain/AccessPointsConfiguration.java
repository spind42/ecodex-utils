package eu.ecodex.utils.monitor.gw.domain;

import lombok.Data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Data
public class AccessPointsConfiguration {

    int id = -1;

    /**
     * The remote access points
     */
    Collection<AccessPoint> remoteAccessPoints = new ArrayList<>();

    /**
     * the own gateway
     */
    AccessPoint self;

}
