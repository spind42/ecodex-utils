package eu.ecodex.utils.spring.quartz.configuration;

import eu.ecodex.utils.spring.quartz.domain.CronJob;
import eu.ecodex.utils.spring.quartz.domain.TriggerAndJobDefinition;
import eu.ecodex.utils.spring.quartz.service.QuartzJobService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import javax.annotation.PostConstruct;
import java.text.ParseException;
import java.util.List;

import static eu.ecodex.utils.spring.quartz.configuration.ScheduledWithQuartzConfiguration.TRIGGER_AND_JOB_DEFINITION_LIST_BEAN_NAME;

/**
 * processes all created TriggerAndJobDefinitions
 * must be called after the ScheduledBeanPostProcessor has been run
 */
public class TriggerAndJobDefinitionProcessor {

    private static final Logger LOGGER = LogManager.getLogger(TriggerAndJobDefinitionProcessor.class);

    @Autowired(required = false)
    @Qualifier(TRIGGER_AND_JOB_DEFINITION_LIST_BEAN_NAME)
    private List<TriggerAndJobDefinition> triggerAndJobDefinitionList;

    @Autowired
//    @Lazy
    SchedulerFactoryBean schedulerFactoryBean;


    @Autowired
    ApplicationContext applicationContext;

    @PostConstruct
    public void postConstruct() {
        triggerAndJobDefinitionList.forEach(this::processTriggerAndJobDefinition);
    }

    private void processTriggerAndJobDefinition(TriggerAndJobDefinition triggerAndJobDefinition) {
        LOGGER.trace("Creating trigger and job for {}", triggerAndJobDefinition);
//        triggerAndJobDefinition.getScheduled().zone();
        String jobName = triggerAndJobDefinition.getBeanName();
        String methodName = triggerAndJobDefinition.getMethod().getName();

        JobDetailFactoryBean factoryBean = new JobDetailFactoryBean();
        factoryBean.setJobClass(CronJob.class);
        factoryBean.setDurability(true);
        factoryBean.setApplicationContext(applicationContext);
        factoryBean.setName(jobName);
        factoryBean.setGroup("defaultGroup");
//        factoryBean.setGroup(jobGroup);

        // set job data map
        JobDataMap jobDataMap = new JobDataMap();
//        jobDataMap.put("myKey", "myValue");
        jobDataMap.put("method", triggerAndJobDefinition.getMethod());
        jobDataMap.put("bean", triggerAndJobDefinition.getBean());
        factoryBean.setJobDataMap(jobDataMap);

        factoryBean.afterPropertiesSet();
//        JobDetail object = factoryBean.getObject();

//        Trigger trigger =

        PersistableCronTriggerFactoryBean triggerFactoryBean = new PersistableCronTriggerFactoryBean();
        triggerFactoryBean.setName(jobName);
//        triggerFactoryBean.setStartTime(startTime);
        triggerFactoryBean.setCronExpression(triggerAndJobDefinition.getScheduled().cron());
//        triggerFactoryBean.setMisfireInstruction(misFireInstruction);
        try {
            triggerFactoryBean.afterPropertiesSet();
        } catch (ParseException e) {
            e.printStackTrace();
        }


        try {
            schedulerFactoryBean.getScheduler().scheduleJob(factoryBean.getObject(), triggerFactoryBean.getObject());
        } catch (SchedulerException e) {
            LOGGER.error(e);
            throw new RuntimeException(e);
        }




    }

}
