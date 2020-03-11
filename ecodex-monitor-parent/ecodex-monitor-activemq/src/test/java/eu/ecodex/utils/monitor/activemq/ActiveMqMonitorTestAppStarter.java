package eu.ecodex.utils.monitor.activemq;

import eu.ecodex.utils.spring.starter.SpringBootWarOnTomcatStarter;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

import java.util.Properties;

//@SpringBootApplication
public class ActiveMqMonitorTestAppStarter extends SpringBootWarOnTomcatStarter {

    public static void main(String... args) {
        SpringBootWarOnTomcatStarter springBootWarOnTomcatStarter = new ActiveMqMonitorTestAppStarter();
        springBootWarOnTomcatStarter.run(args);
    }

    @Override
    protected Class<?>[] getSources() {
        return new Class<?>[] {ActiveMqMonitorAppStarter.class};
    }

    @Override
    protected void configureApplicationContext(SpringApplicationBuilder application, Properties springProperties) {
        application.profiles("dev", "test");
    }


}
