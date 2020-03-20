package eu.ecodex.utils.monitor.gw.service;


import eu.ecodex.utils.monitor.gw.config.GatewayMonitorConfigurationProperties;
import eu.ecodex.utils.monitor.gw.domain.AccessPoint;
import eu.ecodex.utils.monitor.gw.domain.AccessPointsConfiguration;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.Collection;

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

    @PostConstruct
    public void init() {
        updateConfiguredGateways();
    }

    public synchronized void updateConfiguredGateways() {
        this.accesPointConfig = null;
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
        return this.accesPointConfig.getRemoteAccessPoints();
    }

    public synchronized AccessPoint getSelf() {
        return this.accesPointConfig.getSelf();
    }


}
