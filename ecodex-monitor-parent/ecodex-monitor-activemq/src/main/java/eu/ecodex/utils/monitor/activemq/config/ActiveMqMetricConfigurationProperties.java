package eu.ecodex.utils.monitor.activemq.config;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = ActiveMqMetricConfigurationProperties.PREFIX)
public class ActiveMqMetricConfigurationProperties {

    public static final String PREFIX = ActiveMqEndpointConfigurationProperties.ACTIVEMQ_MONITOR_PREFIX + ".metrics";

    boolean enabled = true;



}
