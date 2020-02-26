package eu.ecodex.utils.monitor.keystores;

import eu.ecodex.utils.monitor.keystores.config.CertificateConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

@Configuration
@Conditional(ConditionalOnCertificatesCheckEnabled.class)
@EnableConfigurationProperties(CertificateConfigurationProperties.class)
@ComponentScan(basePackageClasses = CertificateMonitorAutoConfiguration.class)
public class CertificateMonitorAutoConfiguration {

}
