package eu.ecodex.utils.monitor.keystores;

import eu.ecodex.utils.spring.starter.SpringBootWarOnTomcatStarter;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CertificateMonitorAppStarter extends SpringBootWarOnTomcatStarter {

    public static void main(String... args) {
        SpringBootWarOnTomcatStarter springBootWarOnTomcatStarter = new CertificateMonitorAppStarter();
        springBootWarOnTomcatStarter.run(args);
    }

    @Override
    protected Class<?>[] getSources() {
        return new Class<?>[] {CertificateMonitorAppStarter.class};
    }
}
