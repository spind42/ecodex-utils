package eu.ecodex.utils.monitor.app.activemq.dto;

import lombok.Data;

@Data
public class DestinationInfo {

    private String name;

    private long queueSize;

    private DestinationType type;

    private long enqueueCount;

    private long dispatchCount;

    private long dequeueCount;

    private long storeMessageSize;

    private long maxEnqueueTime;

    public static enum DestinationType {
        QUEUE, TOPIC, NOT_KNOWN;
    }

}
