package eu.ecodex.utils.monitor.activemq.service;

import eu.ecodex.utils.monitor.activemq.dto.DestinationInfo;
import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.apache.activemq.broker.jmx.DestinationViewMBean;
import org.apache.activemq.web.BrokerFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class ActiveMqMetricService {

    @Autowired
    DestinationService destinationService;

    @Autowired
    MeterRegistry meterRegistry;

//    @Autowired
//    BrokerFacade brokerFacade;


    List<DestinationViewMBean> destinations = new ArrayList<>();

    @PostConstruct
    public void init() throws Exception {
        destinationService
                .getDestinations()
                .stream()
                .forEach(this::addMetric);

    }

    private void addMetric(DestinationViewMBean dst) {

        //Stream<String> queueSize = Stream.of("queueSize", "maxPageSize");

        Gauge.Builder<DestinationViewMBean> builder = Gauge.builder("activemq.destinations." + dst.getName() + ".queueSize", dst, DestinationViewMBean::getQueueSize);
        builder.description("Number of messages on this destination, including any that have been dispatched but not acknowledged");
        builder.baseUnit("message");
        builder.register(meterRegistry);

        Gauge.Builder<DestinationViewMBean> builder1 = Gauge.builder("activemq.destinations." + dst.getName() + ".maxPageSize", dst, DestinationViewMBean::getMaxPageSize);
        builder1.description("Maximum number of messages to be paged in");
        builder1.register(meterRegistry);


//        meterRegistry.gauge("activemq.destinations." + dst.getName(), dst, DestinationViewMBean::getQueueSize);

    }

}
