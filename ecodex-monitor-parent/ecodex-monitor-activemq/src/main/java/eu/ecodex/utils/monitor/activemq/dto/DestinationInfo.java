package eu.ecodex.utils.monitor.activemq.dto;

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

    private long memoryLimit;

    private long maxEnqueueTime;

    private long tempUsageLimit;

    private long maxPageSize;

    public static enum DestinationType {
        QUEUE, TOPIC, NOT_KNOWN;
    }

}
