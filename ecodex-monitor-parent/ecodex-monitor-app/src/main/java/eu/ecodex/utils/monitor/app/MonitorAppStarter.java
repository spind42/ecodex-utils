package eu.ecodex.utils.monitor.app;

import eu.ecodex.utils.spring.starter.SpringBootWarOnTomcatStarter;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jms.activemq.ActiveMQAutoConfiguration;


public class MonitorAppStarter {

    public static void main(String... args) {
        MonitorAppConfiguration monitorAppConfiguration = new MonitorAppConfiguration();
        monitorAppConfiguration.run(args);
    }

}
