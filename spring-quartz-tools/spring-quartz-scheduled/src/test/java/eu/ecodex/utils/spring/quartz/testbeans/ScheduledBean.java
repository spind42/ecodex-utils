package eu.ecodex.utils.spring.quartz.testbeans;

import eu.ecodex.utils.spring.quartz.annotation.QuartzScheduled;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.springframework.stereotype.Component;


@Component
public class ScheduledBean {

    private static final Logger LOGGER = LogManager.getLogger(ScheduledBean.class);

    public static final String SCHEDULED_CRON_JOB_STRING = "scheduledCronJob";
    public static final String SCHEDULED_FIXED_RATE_JOB_STRING = "scheduledFixedRateJob";

//    @Scheduled(cron = "* * * * * ?")
    @QuartzScheduled(cron = ScheduledBeanConfigurationProperties.class)
    public void scheduledCronJob() {
        long now = System.currentTimeMillis() / 1000;
        LOGGER.info("schedule tasks using cron jobs [{}] - {}", SCHEDULED_CRON_JOB_STRING, now);
    }

//    @Scheduled(fixedDelay = 1000)
    @QuartzScheduled(fixedRate = ScheduledBeanConfigurationProperties.class)
    public void scheduledFixedRateJob() {
        long now = System.currentTimeMillis() / 1000;
        LOGGER.info("schedule tasks using cron jobs [{}] - {}", SCHEDULED_FIXED_RATE_JOB_STRING, now);
    }

}
