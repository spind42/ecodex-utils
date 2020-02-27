package eu.ecodex.utils.monitor.activemq;

import eu.ecodex.utils.monitor.activemq.config.ActiveMqEndpointConfigurationProperties;
import eu.ecodex.utils.monitor.activemq.config.ActiveMqMetricConfigurationProperties;
import eu.ecodex.utils.monitor.activemq.service.ActiveMqMetricService;
import eu.ecodex.utils.monitor.activemq.service.ActiveMqQueuesMonitorEndpoint;
import eu.ecodex.utils.monitor.activemq.service.DestinationService;
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

@Configuration
@EnableConfigurationProperties(ActiveMqEndpointConfigurationProperties.class)
@ConditionalOnProperty(prefix = ActiveMqEndpointConfigurationProperties.ACTIVEMQ_MONITOR_PREFIX, name = "enabled", havingValue = "true")
@ComponentScan(basePackageClasses = ActiveMqEndpointAutoConfiguration.class)
public class ActiveMqEndpointAutoConfiguration {

    @Configuration

//    @ConditionalOnProperty(prefix = ActiveMqEndpointConfigurationProperties.ACTIVEMQ_MONITOR_PREFIX, name = "enabled", havingValue = "true")
    @ConditionalOnProperty(prefix = ActiveMqMetricConfigurationProperties.PREFIX, name = "enabled", havingValue = "true")
    @EnableConfigurationProperties(ActiveMqMetricConfigurationProperties.class)
    public static class MetricConfiguration {
        @Bean
        @Lazy(false)
        ActiveMqMetricService activeMqMetricService() {
            return new ActiveMqMetricService();
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
    @Conditional(JmxUrlNotEmptyCondition.class)
    BrokerFacade jmxBrokerFacade() {
//        String jmxUrl = configurationProperties.getJmxUrl();

        RemoteJMXBrokerFacade remoteJMXBrokerFacade = new RemoteJMXBrokerFacade();
        remoteJMXBrokerFacade.setBrokerName("broker");
        remoteJMXBrokerFacade.setConfiguration(getWebConsoleConfiguration());

        return remoteJMXBrokerFacade;
    }


    @Bean
    @ConditionalOnMissingBean
    BrokerFacade localBrokerFacade() {
        SingletonBrokerFacade brokerFacade = new SingletonBrokerFacade();
        return brokerFacade;
    }

    private WebConsoleConfiguration getWebConsoleConfiguration() {

        return new WebConsoleConfiguration() {
            @Override
            public ConnectionFactory getConnectionFactory() {
                return null;
            }

            @Override
            public Collection<JMXServiceURL> getJmxUrls() {
                return configurationProperties.getJmxUrl();
            }

            @Override
            public String getJmxUser() {
                return configurationProperties.getJmxUser();
            }

            @Override
            public String getJmxPassword() {
                return configurationProperties.getJmxPassword();
            }
        };
    }

    public static class JmxUrlNotEmptyCondition implements Condition {

        @Override
        public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
            Bindable<ActiveMqEndpointConfigurationProperties> bindable = Bindable.of(ActiveMqEndpointConfigurationProperties.class);
            Binder binder = Binder.get(context.getEnvironment());
            BindResult<ActiveMqEndpointConfigurationProperties> bindResult = binder.bind(ActiveMqEndpointConfigurationProperties.ACTIVEMQ_MONITOR_PREFIX, bindable, null);
            ActiveMqEndpointConfigurationProperties bean = bindResult.orElse(null);
            return bean != null && bean.getJmxUrl().size() > 0;
        }
    }

}
