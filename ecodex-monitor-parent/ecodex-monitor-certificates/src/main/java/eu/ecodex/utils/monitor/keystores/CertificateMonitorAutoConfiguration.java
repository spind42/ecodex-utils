package eu.ecodex.utils.monitor.keystores;

import eu.ecodex.utils.monitor.keystores.config.CertificateConfigurationProperties;
import eu.ecodex.utils.monitor.keystores.service.CertificateHealthIndicator;
import eu.ecodex.utils.monitor.keystores.service.CertificatesEndpoint;
import eu.ecodex.utils.monitor.keystores.service.KeyService;
import eu.ecodex.utils.monitor.keystores.service.crtprocessor.X509CertificateToStoreEntryInfoProcessorImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

@Configuration
//@Conditional(ConditionalOnCertificatesCheckEnabled.class)
@ConditionalOnProperty(prefix = CertificateConfigurationProperties.CERTIFICATE_MONITOR_PREFIX, name = "enabled", havingValue = "true")
@EnableConfigurationProperties(CertificateConfigurationProperties.class)
@ComponentScan(basePackageClasses = X509CertificateToStoreEntryInfoProcessorImpl.class)
public class CertificateMonitorAutoConfiguration {

    @Bean
    CertificateHealthIndicator certificateHealthIndicator() {
        return new CertificateHealthIndicator();
    }

    @Bean
    KeyService keyService() {
        return new KeyService();
    }

    @Bean
    CertificatesEndpoint certificatesEndpoint() {
        return new CertificatesEndpoint();
    }



}
