package eu.ecodex.utils.monitor.app.activemq;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.management.remote.JMXServiceURL;
import java.util.ArrayList;
import java.util.List;

@ConfigurationProperties(prefix = ActiveMqEndpointConfigurationProperties.ACTIVEMQ_MONITOR_PREFIX)
@Data
public class ActiveMqEndpointConfigurationProperties {

    public static final String ACTIVEMQ_MONITOR_PREFIX = "monitor.activemq";

    private List<JMXServiceURL> jmxUrl = new ArrayList<>();

    private String jmxUser;

    private String jmxPassword;


}
