package eu.ecodex.utils.spring.quartz.configuration;

import org.springframework.scheduling.quartz.CronTriggerFactoryBean;

import java.text.ParseException;

/**
 * Needed to set Quartz useProperties=true when using Spring classes,
 * because Spring sets an object reference on JobDataMap that is not a String
 *
 * @see <a href="http://site.trimplement.com/using-spring-and-quartz-with-jobstore-properties">http://site.trimplement.com/using-spring-and-quartz-with-jobstore-properties/</a>
 * @see <a href="http://forum.springsource.org/showthread.php?130984-Quartz-error-IOException">http://forum.springsource.org/showthread.php?130984-Quartz-error-IOException</a>
 */
public class PersistableCronTriggerFactoryBean extends CronTriggerFactoryBean {

    public static final String JOB_DETAIL_KEY = "jobDetail";

    @Override
    public void afterPropertiesSet() throws ParseException {
        super.afterPropertiesSet();
        //Remove the JobDetail element
        getJobDataMap().remove(JOB_DETAIL_KEY);
    }

}
