package eu.ecodex.utils.monitor.activemq.service;

import eu.ecodex.utils.monitor.activemq.dto.DestinationInfo;
import org.apache.activemq.broker.jmx.DestinationViewMBean;
import org.apache.activemq.broker.jmx.QueueViewMBean;
import org.apache.activemq.broker.jmx.TopicViewMBean;
import org.apache.activemq.web.BrokerFacade;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class DestinationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DestinationService.class);

    @Autowired
    BrokerFacade activeMqBrokerFacade;

    List<DestinationViewMBean> destinations = new ArrayList<>();

    @PostConstruct
    public void init() {
        try {
            destinations.addAll(activeMqBrokerFacade.getQueues());
            destinations.addAll(activeMqBrokerFacade.getTopics());
        } catch (Exception e) {
            LOGGER.error("Error while getting destinations from brokerFacade. ActiveMQ Broker Monitoring will not work!", e);
        }
    }


    public List<DestinationViewMBean> getDestinations() {
        return this.destinations;
    }

    public List<DestinationInfo> getDestinationInfos() throws Exception {

        return destinations.stream()
                .map(this::mapToQueueInfo)
                .collect(Collectors.toList());
    }

    private DestinationInfo mapToQueueInfo(DestinationViewMBean dst) {
        DestinationInfo info = new DestinationInfo();
        info.setName(dst.getName());

        info.setQueueSize(dst.getQueueSize());

        if (dst instanceof TopicViewMBean) {
            info.setType(DestinationInfo.DestinationType.TOPIC);
        } else if (dst instanceof QueueViewMBean) {
            info.setType(DestinationInfo.DestinationType.QUEUE);
        } else {
            info.setType(DestinationInfo.DestinationType.NOT_KNOWN);
        }

        info.setDequeueCount(dst.getDequeueCount());
        info.setDispatchCount(dst.getDispatchCount());
        info.setEnqueueCount(dst.getEnqueueCount());
        info.setMaxEnqueueTime(dst.getMaxEnqueueTime());
        info.setStoreMessageSize(dst.getStoreMessageSize());
        info.setMemoryLimit(dst.getMemoryLimit());
        info.setTempUsageLimit(dst.getTempUsageLimit());
        info.setMaxPageSize(dst.getMaxPageSize());

        return info;
    }

}

