package eu.ecodex.utils.monitor.activemq;

import eu.ecodex.utils.monitor.activemq.config.ActiveMqEndpointConfigurationProperties;
import eu.ecodex.utils.monitor.activemq.config.ActiveMqHealthChecksConfigurationProperties;
import eu.ecodex.utils.monitor.activemq.config.ActiveMqMetricConfigurationProperties;
import eu.ecodex.utils.monitor.activemq.service.*;
import io.micrometer.core.instrument.util.StringUtils;
import org.apache.activemq.broker.BrokerRegistry;
import org.apache.activemq.web.BrokerFacade;
import org.apache.activemq.web.RemoteJMXBrokerFacade;
import org.apache.activemq.web.SingletonBrokerFacade;
import org.apache.activemq.web.config.WebConsoleConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.bind.BindResult;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.annotation.*;
import org.springframework.core.type.AnnotatedTypeMetadata;

import javax.jms.ConnectionFactory;
import javax.management.remote.JMXServiceURL;
import java.util.Collection;
import java.util.Optional;

@Configuration
@EnableConfigurationProperties(ActiveMqEndpointConfigurationProperties.class)
@ConditionalOnProperty(prefix = ActiveMqEndpointConfigurationProperties.ACTIVEMQ_MONITOR_PREFIX, name = "enabled", havingValue = "true")
@ComponentScan(basePackageClasses = ActiveMqEndpointAutoConfiguration.class)
public class ActiveMqEndpointAutoConfiguration {

    @Configuration
    @ConditionalOnProperty(prefix = ActiveMqMetricConfigurationProperties.PREFIX, name = "enabled", havingValue = "true")
    @EnableConfigurationProperties(ActiveMqMetricConfigurationProperties.class)
    public static class MetricConfiguration {
        @Bean
        @Lazy(false)
        ActiveMqMetricService activeMqMetricService() {
            return new ActiveMqMetricService();
        }
    }


    @Configuration
    @ConditionalOnProperty(prefix = ActiveMqHealthChecksConfigurationProperties.PREFIX, name = "enabled", havingValue = "true")
    @EnableConfigurationProperties(ActiveMqHealthChecksConfigurationProperties.class)
    public static class HealthConfiguration {
        @Bean
        @Lazy(false)
        ActiveMqHealthService activeMqHealthService() {
            return new ActiveMqHealthService();
        }
    }




    @Autowired
    ActiveMqEndpointConfigurationProperties configurationProperties;

    @Bean
    ActiveMqQueuesMonitorEndpoint monitorEndpoint() {
        return new ActiveMqQueuesMonitorEndpoint();
    }

    @Bean
    DestinationService destinationService() {
        return new DestinationService();
    }

    @Bean
    BrokerFacadeFactory brokerFacadeFactory() {
        return new BrokerFacadeFactory();
    }

//    @Bean
//    BrokerFacade brokerFacade() throws Exception {
//        return brokerFacadeFactory().getObject();
//    }

    @Bean
    @ConditionalOnMissingBean
    BrokerFacade localBrokerFacade() {
        SingletonBrokerFacade brokerFacade = new SingletonBrokerFacade();
        return brokerFacade;
    }


    public abstract static class ActiveMqEndpointConfigurationPropertiesCondition implements Condition {
        Optional<ActiveMqEndpointConfigurationProperties> getProps(ConditionContext context) {
            Bindable<ActiveMqEndpointConfigurationProperties> bindable = Bindable.of(ActiveMqEndpointConfigurationProperties.class);
            Binder binder = Binder.get(context.getEnvironment());
            BindResult<ActiveMqEndpointConfigurationProperties> bindResult = binder.bind(ActiveMqEndpointConfigurationProperties.ACTIVEMQ_MONITOR_PREFIX, bindable, null);
            return Optional.ofNullable(bindResult.orElse(null));
        }
    }

    public static class JmxUrlNotEmptyCondition extends ActiveMqEndpointConfigurationPropertiesCondition {

        @Override
        public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
            Optional<ActiveMqEndpointConfigurationProperties> bean = getProps(context);
            return bean.isPresent() && bean.get().getJmxUrl().size() > 0;
        }
    }

    public static class BrokerNameNotEmptyCondition extends ActiveMqEndpointConfigurationPropertiesCondition {

        @Override
        public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
            Optional<ActiveMqEndpointConfigurationProperties> bean = getProps(context);
            return bean.isPresent() && StringUtils.isNotEmpty(bean.get().getBrokerName());
        }
    }

}
