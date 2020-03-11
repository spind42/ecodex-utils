package eu.ecodex.utils.monitor.activemq.service;

import eu.ecodex.utils.monitor.activemq.dto.DestinationInfo;
import org.apache.activemq.broker.jmx.DestinationViewMBean;
import org.apache.activemq.broker.jmx.QueueViewMBean;
import org.apache.activemq.broker.jmx.TopicViewMBean;
import org.apache.activemq.web.BrokerFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Endpoint(id = ActiveMqQueuesMonitorEndpoint.ENDPOINT_ID)
public class ActiveMqQueuesMonitorEndpoint {


    public static final String ENDPOINT_ID = "activemqdestinations";

    @Autowired
    BrokerFacade activeMqBrokerFacade;


    @ReadOperation
    public List<DestinationInfo> getDestinationInfos() throws Exception {
        List<DestinationViewMBean> destinations = new ArrayList<>();

        destinations.addAll(activeMqBrokerFacade.getQueues());
        destinations.addAll(activeMqBrokerFacade.getTopics());

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
