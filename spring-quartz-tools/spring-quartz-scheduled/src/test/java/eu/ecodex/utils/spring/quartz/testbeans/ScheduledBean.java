package eu.ecodex.utils.spring.quartz.testbeans;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


@Component
public class ScheduledBean {

    private static final Logger LOGGER = LogManager.getLogger(ScheduledBean.class);

    @Scheduled(cron = "* * * * * ?")
    public void scheduledJob() {
        long now = System.currentTimeMillis() / 1000;
        LOGGER.info("schedule tasks using cron jobs - " + now);

    }

}
