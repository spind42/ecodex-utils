package eu.ecodex.utils.monitor.app.activemq;

import eu.ecodex.utils.monitor.app.activemq.dto.QueueInfo;
import org.apache.activemq.broker.jmx.QueueViewMBean;
import org.apache.activemq.web.BrokerFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Endpoint(id = "activemq/queues")
public class ActiveMqQueuesMonitorEndpoint {


    @Autowired
    BrokerFacade activeMqBrokerFacade;


    @ReadOperation
    public List<QueueInfo> getQueues() throws Exception {
        Collection<QueueViewMBean> queues = activeMqBrokerFacade.getQueues();
        return queues.stream()
                .map(this::mapToQueueInfo)
                .collect(Collectors.toList());
    }

    private QueueInfo mapToQueueInfo(QueueViewMBean queueMBean) {
        QueueInfo info = new QueueInfo();
        String name = queueMBean.getName();
        return info;
    }


}
