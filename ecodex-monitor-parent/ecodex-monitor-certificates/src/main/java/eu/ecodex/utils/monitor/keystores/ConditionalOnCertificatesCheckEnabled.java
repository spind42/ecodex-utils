package eu.ecodex.utils.monitor.keystores;

import eu.ecodex.utils.monitor.keystores.config.CertificateConfigurationProperties;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

public class ConditionalOnCertificatesCheckEnabled implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        String enabled = context.getEnvironment().getProperty(CertificateConfigurationProperties.CERTIFICATE_MONITOR_PREFIX + ".enabled", "false");
        return "true".equalsIgnoreCase(enabled);
    }
}
