package eu.ecodex.utils.monitor.gw.service;


import eu.ecodex.utils.monitor.gw.config.GatewayMonitorConfigurationProperties;
import eu.ecodex.utils.monitor.gw.domain.AccessPoint;
import eu.ecodex.utils.monitor.gw.domain.AccessPointsConfiguration;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static eu.ecodex.utils.monitor.gw.config.GatewayMonitorConfigurationProperties.GATEWAY_MONITOR_PREFIX;

public class ConfiguredGatewaysService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfiguredGatewaysService.class);

    @Autowired
    private GatewayMonitorConfigurationProperties monitorConfigurationProperties;

    @Autowired
    private PModeDownloader pModeDownloader;

    private AccessPointsConfiguration accesPointConfig = new AccessPointsConfiguration();

    public void setMonitorConfigurationProperties(GatewayMonitorConfigurationProperties monitorConfigurationProperties) {
        this.monitorConfigurationProperties = monitorConfigurationProperties;
    }

    public synchronized void updateConfiguredGateways() {
        if (monitorConfigurationProperties.getRest().isLoadPmodes()) {
            this.accesPointConfig = pModeDownloader.updateAccessPointsConfig(accesPointConfig);
            LOGGER.info("Loaded configured access points from gateway p-Modes");
        } else if (monitorConfigurationProperties.getAccessPoints() != null) {
            this.accesPointConfig = monitorConfigurationProperties.getAccessPoints();
            LOGGER.info("Loaded configured access points from properties!");
        } else {
            throw new RuntimeException("No access points are configured in properties under: " +
                    "\n[" + GATEWAY_MONITOR_PREFIX + ".access-points] neither is " +
                    "\n loading config from p-modes enabled:" +
                    "\n[" + GATEWAY_MONITOR_PREFIX + ".rest.load-pmodes] is false");
        }
    }

    public synchronized Collection<AccessPoint> getConfiguredGateways() {
        updateConfiguredGateways();
        return this.accesPointConfig.getRemoteAccessPoints();
    }

    public synchronized Collection<AccessPoint> getConfiguredGatewaysWithSelf() {
        updateConfiguredGateways();
        List<AccessPoint> list = new ArrayList<>();
        list.add(getSelf());
        list.addAll(this.accesPointConfig.getRemoteAccessPoints());
        return list;
    }

    public synchronized AccessPoint getSelf() {
        updateConfiguredGateways();
        return this.accesPointConfig.getSelf();
    }

    /**
     *
     * @param name the accesspoint name
     * @return the accesspoint with the name or null if none found
     */
    public synchronized AccessPoint getByName(String name) {
        updateConfiguredGateways();
        if (StringUtils.isEmpty(name)) {
            throw new IllegalArgumentException("Name is not allowed to be empty!");
        }
        if (name.equals(this.accesPointConfig.getSelf().getName())) {
            return this.accesPointConfig.getSelf();
        }
        Optional<AccessPoint> first = this.accesPointConfig.getRemoteAccessPoints()
                .stream()
                .filter(ap -> name.equals(ap.getName()))
                .findFirst();

        return first.orElse(null);
    }


}
