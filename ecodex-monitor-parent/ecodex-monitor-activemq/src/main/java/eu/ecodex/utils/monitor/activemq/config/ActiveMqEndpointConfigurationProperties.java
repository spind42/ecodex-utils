package eu.ecodex.utils.monitor.activemq.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.management.remote.JMXServiceURL;
import java.util.ArrayList;
import java.util.List;

@ConfigurationProperties(prefix = ActiveMqEndpointConfigurationProperties.ACTIVEMQ_MONITOR_PREFIX)
@Data
public class ActiveMqEndpointConfigurationProperties {

    public static final String ACTIVEMQ_MONITOR_PREFIX = "monitor.activemq";

    private boolean enabled = false;

    private boolean localJmx = true;

    private List<JMXServiceURL> jmxUrl = new ArrayList<>();

    private String brokerName;

    private String jmxUser;

    private String jmxPassword;


}
