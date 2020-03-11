package eu.ecodex.utils.monitor.activemq.service;


import eu.ecodex.utils.monitor.activemq.config.ActiveMqEndpointConfigurationProperties;
import org.apache.activemq.broker.jmx.*;
import org.apache.activemq.web.*;
import org.apache.activemq.web.config.WebConsoleConfiguration;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;

import javax.jms.ConnectionFactory;
import javax.management.*;
import javax.management.remote.JMXServiceURL;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public class BrokerFacadeFactory implements FactoryBean<BrokerFacade> {

    private static final Logger LOGGER = LoggerFactory.getLogger(BrokerFacadeFactory.class);


    @Autowired
    ActiveMqEndpointConfigurationProperties configurationProperties;

    BrokerFacade facade;

    @Override
    public BrokerFacade getObject() throws Exception {
        if (facade == null) {
            initFacade();
        }
        return facade;
    }



    @Override
    public Class<?> getObjectType() {
        return BrokerFacade.class;
    }

    private void initFacade() {
        if (configurationProperties.getJmxUrl().size() > 0) {
            LOGGER.info("jmx url is present, creating RemoteJMXBrokerFacade");
            RemoteJMXBrokerFacade brokerFacade = jmxBrokerFacade();
            brokerFacade.setBrokerName(configurationProperties.getBrokerName());
            facade = brokerFacade;
            return;
        }

        if (configurationProperties.isLocalJmx()) {
            LOGGER.info("local jmx is activated creating JmxBrokerFacade");
            JmxLocalBrokerFacade jmxBrokerFacade = new JmxLocalBrokerFacade();
            jmxBrokerFacade.setBrokerName(configurationProperties.getBrokerName());
            facade = jmxBrokerFacade;
            return;
        }

        LOGGER.info("Falling back to SingletonBrokerFacade");
        SingletonBrokerFacade singletonBrokerFacade = new SingletonBrokerFacade();
        facade = singletonBrokerFacade;

    }

    private RemoteJMXBrokerFacade jmxBrokerFacade() {

        RemoteJMXBrokerFacade remoteJMXBrokerFacade = new RemoteJMXBrokerFacade();
        remoteJMXBrokerFacade.setBrokerName("broker");
        remoteJMXBrokerFacade.setConfiguration(getWebConsoleConfiguration());

        return remoteJMXBrokerFacade;
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

    private static class JmxLocalBrokerFacade extends RemoteJMXBrokerFacade {

        @Override
        public BrokerViewMBean getBrokerAdmin() throws Exception {
            MBeanServer platformMBeanServer = ManagementFactory.getPlatformMBeanServer();

            Set<ObjectName> brokers = findBrokers(platformMBeanServer);
            if (brokers.size() == 0) {
                throw new IOException("No broker could be found in the JMX.");
            }
            ObjectName name = brokers.iterator().next();
            BrokerViewMBean mbean = MBeanServerInvocationHandler.newProxyInstance(platformMBeanServer, name, BrokerViewMBean.class, true);
            return mbean;
        }

        @Override
        public Set queryNames(ObjectName name, QueryExp query) throws Exception {
            MBeanServer platformMBeanServer = ManagementFactory.getPlatformMBeanServer();
            return platformMBeanServer.queryNames(name, query);
        }

        @Override
        protected <T> Collection<T> getManagedObjects(ObjectName[] names, Class<T> type) {
            MBeanServer platformMBeanServer = ManagementFactory.getPlatformMBeanServer();

            List<T> answer = new ArrayList<T>();
            if (platformMBeanServer != null) {
                for (int i = 0; i < names.length; i++) {
                    ObjectName name = names[i];
                    T value = MBeanServerInvocationHandler.newProxyInstance(platformMBeanServer, name, type, true);
                    if (value != null) {
                        answer.add(value);
                    }
                }
            }
            return answer;
        }


    }


}
