package eu.ecodex.utils.monitor.app.keystores;

import eu.ecodex.utils.monitor.app.keystores.config.CertificateConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(CertificateConfigurationProperties.class)
public class CertificateMonitorConfiguration {



}
