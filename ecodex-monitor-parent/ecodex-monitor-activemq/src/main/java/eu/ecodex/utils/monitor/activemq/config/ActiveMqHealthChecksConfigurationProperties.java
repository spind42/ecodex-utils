package eu.ecodex.utils.monitor.activemq.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;


@Data
@ConfigurationProperties(prefix = ActiveMqHealthChecksConfigurationProperties.PREFIX)
public class ActiveMqHealthChecksConfigurationProperties {

    public static final String PREFIX = ActiveMqEndpointConfigurationProperties.ACTIVEMQ_MONITOR_PREFIX + ".health";

    private boolean enabled = true;

    private float queueSizeWarnThreshold = 0.6f;
    private float queueSizeErrorThreshold = 0.8f;

}
