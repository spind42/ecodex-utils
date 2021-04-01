package eu.ecodex.utils.spring.quartz.testbeans;

import eu.ecodex.utils.spring.quartz.annotation.CronStringProvider;
import eu.ecodex.utils.spring.quartz.annotation.IntervalProvider;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.Duration;

@ConfigurationProperties(prefix = "test")
@Component
public class ScheduledBeanConfigurationProperties implements IntervalProvider, CronStringProvider {

    private String cronString;
    private Duration interval;

    @Override
    public String getCronString() {
        return cronString;
    }

    public void setCronString(String cronString) {
        this.cronString = cronString;
    }

    @Override
    public Duration getInterval() {
        return interval;
    }

    public void setInterval(Duration interval) {
        this.interval = interval;
    }
}
