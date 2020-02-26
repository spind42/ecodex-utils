package eu.ecodex.utils.monitor.activemq;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

public class ActiveMqMonitoringEnabledCondition implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        String enabled = context.getEnvironment().getProperty(ActiveMqEndpointConfigurationProperties.ACTIVEMQ_MONITOR_PREFIX + ".enabled", "false");
        return "true".equalsIgnoreCase(enabled);
    }
}
