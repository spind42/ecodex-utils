package eu.ecodex.utils.spring.quartz.configuration;

import eu.ecodex.utils.spring.quartz.domain.CronJob;
import eu.ecodex.utils.spring.quartz.domain.TriggerAndJobDefinition;
import eu.ecodex.utils.spring.quartz.service.QuartzJobService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.*;
import org.quartz.impl.triggers.SimpleTriggerImpl;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.scheduling.quartz.SimpleTriggerFactoryBean;

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
        String beanName = triggerAndJobDefinition.getBeanName();
        String methodName = triggerAndJobDefinition.getMethod().getName();

        String jobName = String.format("%s_%s", beanName, methodName);

        JobDetailFactoryBean factoryBean = new JobDetailFactoryBean();
        factoryBean.setJobClass(CronJob.class);
        factoryBean.setDurability(true);
        factoryBean.setApplicationContext(applicationContext);
        factoryBean.setName(jobName);
        factoryBean.setGroup("defaultGroup");


        // set job data map
        JobDataMap jobDataMap = new JobDataMap();
//        jobDataMap.put("myKey", "myValue");
        jobDataMap.put("method", triggerAndJobDefinition.getMethod());
        jobDataMap.put("bean", triggerAndJobDefinition.getBean());
        factoryBean.setJobDataMap(jobDataMap);

        factoryBean.afterPropertiesSet();


        FactoryBean<? extends Trigger> triggerFactoryBean = null;

//        triggerFactoryBean.setStartTime(startTime);

        Scheduled scheduled = triggerAndJobDefinition.getScheduled();
        if (scheduled.cron().isEmpty() && scheduled.fixedRate() >= 0) {
            SimpleTriggerFactoryBean simpleTriggerFactoryBean = new SimpleTriggerFactoryBean();
            simpleTriggerFactoryBean.setRepeatInterval(scheduled.fixedRate());
            simpleTriggerFactoryBean.setName(jobName);

            triggerFactoryBean = simpleTriggerFactoryBean;
        } else if (scheduled.cron().isEmpty() && scheduled.fixedDelay() >= 0) {
                SimpleTriggerFactoryBean simpleTriggerFactoryBean = new SimpleTriggerFactoryBean();
                simpleTriggerFactoryBean.setRepeatInterval(scheduled.fixedDelay());
                simpleTriggerFactoryBean.setName(jobName);
                triggerFactoryBean = simpleTriggerFactoryBean;
        } else if (!scheduled.cron().isEmpty() && !("-".equals(scheduled.cron()))) { //it is a cron string if not empty and not "-"
            CronTriggerFactoryBean cronTriggerFactoryBean = new CronTriggerFactoryBean();
            cronTriggerFactoryBean.setCronExpression(triggerAndJobDefinition.getScheduled().cron());
            cronTriggerFactoryBean.setName(jobName);
            triggerFactoryBean = cronTriggerFactoryBean;
        } else {
            LOGGER.error("Cannot handle annotation scheduled on Bean [{}] with class [{}] method [{}]\n" +
                            "Annotation will be ignored - no trigger and job is generated for this annotation!",
                    triggerAndJobDefinition.getBean(),
                    triggerAndJobDefinition.getBean().getClass(),
                    methodName);
        }



//        triggerFactoryBean.setMisfireInstruction(misFireInstruction);



        try {
            ((InitializingBean)triggerFactoryBean).afterPropertiesSet();
            schedulerFactoryBean.getScheduler().scheduleJob(factoryBean.getObject(), triggerFactoryBean.getObject());
        } catch (ParseException e) {
            LOGGER.error("Unable to parse cron string", e);
            throw new RuntimeException(e);
        } catch (SchedulerException e) {
            LOGGER.error(e);
            throw new RuntimeException(e);
        } catch (Exception e) {
            LOGGER.error(e);
            throw new RuntimeException(e);
        }

    }

}
