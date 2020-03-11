package eu.ecodex.utils.monitor.app;

import eu.ecodex.utils.spring.starter.SpringBootWarOnTomcatStarter;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jms.activemq.ActiveMQAutoConfiguration;

@SpringBootApplication(
        exclude = { ActiveMQAutoConfiguration.class}
)
public class ActiveMqMonitorAppStarter extends SpringBootWarOnTomcatStarter {

    public static void main(String... args) {
        SpringBootWarOnTomcatStarter springBootWarOnTomcatStarter = new ActiveMqMonitorAppStarter();
        springBootWarOnTomcatStarter.run(args);
    }

    @Override
    protected Class<?>[] getSources() {
        return new Class<?>[] {ActiveMqMonitorAppStarter.class};
    }
}
