package eu.ecodex.utils.monitor.app.activemq;

import org.apache.activemq.web.BrokerFacade;
import org.apache.activemq.web.LocalBrokerFacade;
import org.apache.activemq.web.RemoteJMXBrokerFacade;
import org.apache.activemq.web.SingletonBrokerFacade;
import org.apache.activemq.web.config.WebConsoleConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.*;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.util.StringUtils;

import javax.jms.ConnectionFactory;
import javax.management.remote.JMXServiceURL;
import java.util.Collection;

@Configuration
@EnableConfigurationProperties(ActiveMqEndpointConfigurationProperties.class)
public class ActiveMqEndpointConfiguration {

    @Autowired
    ActiveMqEndpointConfigurationProperties configurationProperties;

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
            ActiveMqEndpointConfigurationProperties bean = context.getBeanFactory().getBean(ActiveMqEndpointConfigurationProperties.class);
            return bean.getJmxUrl().size() > 0;
        }
    }

}
