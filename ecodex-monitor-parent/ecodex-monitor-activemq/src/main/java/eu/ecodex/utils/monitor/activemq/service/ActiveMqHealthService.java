package eu.ecodex.utils.monitor.activemq.service;

import eu.ecodex.utils.monitor.activemq.config.ActiveMqHealthChecksConfigurationProperties;
import org.apache.activemq.broker.jmx.DestinationViewMBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health;
import org.springframework.stereotype.Component;

public class ActiveMqHealthService extends AbstractHealthIndicator {

    @Autowired
    DestinationService destinationService;

    @Autowired
    ActiveMqHealthChecksConfigurationProperties config;

    @Override
    protected void doHealthCheck(Health.Builder builder) throws Exception {
        builder.up();

        destinationService
                .getDestinations()
                .forEach(dst -> this.checkDestinationHealth(builder, dst));


    }

    private void checkDestinationHealth(Health.Builder builder, DestinationViewMBean dst) {
        String checkName = dst.getName() + "_usage";

        long queueSize = dst.getQueueSize();
        long maxPageSize = dst.getMaxPageSize();

        long usage = 0;
        if (queueSize > 0) {
             usage = maxPageSize / queueSize;
        }

        builder.withDetail(checkName + "_percentage", usage);
        builder.withDetail(checkName + "_size", queueSize);
        builder.withDetail(checkName + "_maxSize", maxPageSize);
        builder.withDetail(checkName + "_warn", config.getQueueSizeWarnThreshold());
        builder.withDetail(checkName + "_error", config.getQueueSizeErrorThreshold());

        if (usage < config.getQueueSizeWarnThreshold() && usage < config.getQueueSizeErrorThreshold()) {
            builder.withDetail(checkName + "_state", "OK");
            return;
        }

        if (usage < config.getQueueSizeErrorThreshold()) {
            builder.withDetail(checkName + "_state", "WARN");
            return;
        }

        builder.down();
        builder.withDetail(checkName + "_state", "DOWN");

    }

}
