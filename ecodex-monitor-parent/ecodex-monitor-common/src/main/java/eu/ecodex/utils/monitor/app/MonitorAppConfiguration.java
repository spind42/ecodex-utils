package eu.ecodex.utils.monitor.app;

import eu.ecodex.utils.spring.starter.SpringBootWarOnTomcatStarter;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jms.activemq.ActiveMQAutoConfiguration;

@SpringBootApplication(
        exclude = { ActiveMQAutoConfiguration.class}
)
public class MonitorAppConfiguration extends SpringBootWarOnTomcatStarter {

    @Override
    protected Class<?>[] getSources() {
        return new Class<?>[] {MonitorAppConfiguration.class};
    }

}
