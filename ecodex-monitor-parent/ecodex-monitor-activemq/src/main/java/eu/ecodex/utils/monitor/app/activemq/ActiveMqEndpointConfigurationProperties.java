package eu.ecodex.utils.monitor.app.activemq;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = ActiveMqEndpointConfigurationProperties.ACTIVEMQ_MONITOR_PREFIX)
@Data
public class ActiveMqEndpointConfigurationProperties {

    public static final String ACTIVEMQ_MONITOR_PREFIX = "activemq";

    String jmxUrl = "";


}
