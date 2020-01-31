package eu.ecodex.utils.monitor.app;

import eu.ecodex.utils.spring.starter.SpringBootWarOnTomcatStarter;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import sun.security.util.ArrayUtil;

import java.lang.reflect.Array;
import java.util.stream.Stream;

@SpringBootApplication
public class MonitorAppStarter extends SpringBootWarOnTomcatStarter {

    public static void main(String... args) {
        SpringBootWarOnTomcatStarter springBootWarOnTomcatStarter = new MonitorAppStarter();
        springBootWarOnTomcatStarter.run(args);
    }

    @Override
    protected Class<?>[] getSources() {
        return new Class<?>[] {MonitorAppStarter.class};
    }
}
