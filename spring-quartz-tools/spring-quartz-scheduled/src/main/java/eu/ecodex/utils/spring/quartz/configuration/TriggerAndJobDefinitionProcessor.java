package eu.ecodex.utils.spring.quartz.configuration;

import eu.ecodex.utils.spring.quartz.annotation.CronStringProvider;
import eu.ecodex.utils.spring.quartz.annotation.IntervalProvider;
import eu.ecodex.utils.spring.quartz.annotation.QuartzScheduled;
import eu.ecodex.utils.spring.quartz.domain.CronJob;
import eu.ecodex.utils.spring.quartz.domain.TriggerAndJobDefinition;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.*;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SimpleTriggerFactoryBean;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.text.ParseException;
import java.time.Duration;
import java.util.List;

import static eu.ecodex.utils.spring.quartz.configuration.ScheduledWithQuartzConfiguration.TRIGGER_AND_JOB_DEFINITION_LIST_BEAN_NAME;
import static java.time.temporal.ChronoUnit.MILLIS;
import static java.time.temporal.ChronoUnit.SECONDS;

/**
 * processes all created TriggerAndJobDefinitions
 * must be called after the ScheduledBeanPostProcessor has been run
 */
public class TriggerAndJobDefinitionProcessor {

    private static final Logger LOGGER = LogManager.getLogger(TriggerAndJobDefinitionProcessor.class);

    @Autowired
    @Qualifier(TRIGGER_AND_JOB_DEFINITION_LIST_BEAN_NAME)
    private List<TriggerAndJobDefinition> triggerAndJobDefinitionList;

    @Autowired
    Scheduler scheduler;

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




        QuartzScheduled quartzScheduled = triggerAndJobDefinition.getScheduled();
        //check at least one defined
        if (isCronProviderDefined(quartzScheduled) &&
                isFixedRateProviderDefined(quartzScheduled)
        ) {
            String error = String.format("Neither fixedRate() or cron() are defined within @QuartzScheduled annotation!");
            throw new TriggerBeanCreationException(error, quartzScheduled);
        }

        //check both defined
        if (!isCronProviderDefined(quartzScheduled) &&
            !isFixedRateProviderDefined(quartzScheduled)
        ) {
            String error = String.format("Both fixedRate() and cron() are defined within @QuartzScheduled annotation - you cannot use both!");
            throw new TriggerBeanCreationException(error, quartzScheduled);
        }


        if (isCronProviderDefined(quartzScheduled)) {
            triggerFactoryBean = processFixedRate(quartzScheduled);
        }

        if (isFixedRateProviderDefined(quartzScheduled)) {
            triggerFactoryBean = processCronTrigger(quartzScheduled);
        }

        if (triggerFactoryBean == null) {
            throw new IllegalStateException("triggerFactoryBean must not be null!");
        }

        try {
            ((InitializingBean)triggerFactoryBean).afterPropertiesSet();
            scheduler.scheduleJob(factoryBean.getObject(), triggerFactoryBean.getObject());

        } catch (ParseException e) {
            String error = "Unable to parse cron string";
            throw new TriggerBeanCreationException(error, e, quartzScheduled);
        } catch (SchedulerException e) {
            String error = "Scheduler exception";
            throw new TriggerBeanCreationException(error, e, quartzScheduled);
        } catch (Exception e) {
            LOGGER.error(e);
            String error = "Exception";
            throw new TriggerBeanCreationException(error, e, quartzScheduled);
        }

    }

    private boolean isFixedRateProviderDefined(QuartzScheduled quartzScheduled) {
        return quartzScheduled.fixedRate() == IntervalProvider.DefaultIntervalProvider.class;
    }

    private boolean isCronProviderDefined(QuartzScheduled quartzScheduled) {
        return quartzScheduled.cron() == CronStringProvider.DefaultCronStringProvider.class;
    }

    private CronTriggerFactoryBean processCronTrigger(QuartzScheduled quartzScheduled) {
        Class<? extends CronStringProvider> cronProviderClass = quartzScheduled.cron();
        if (isCronProviderDefined(quartzScheduled)) {
            throw new IllegalArgumentException("quartzScheduled.cron() must be defined!");
        }
        CronStringProvider bean = getBean(quartzScheduled.qualifier(), cronProviderClass);
        String cronString = bean.getCronString();
        if (StringUtils.isEmpty(cronString)) {
            throw new TriggerBeanCreationException("Cron string is empty!", quartzScheduled);
        }
        CronTriggerFactoryBean cronTriggerFactoryBean = new CronTriggerFactoryBean();
        cronTriggerFactoryBean.setCronExpression(cronString);
        return cronTriggerFactoryBean;
    }

    private SimpleTriggerFactoryBean processFixedRate(QuartzScheduled quartzScheduled) {
        Class<? extends IntervalProvider> intervalProviderClass = quartzScheduled.fixedRate();
        if (isFixedRateProviderDefined(quartzScheduled)) {
            throw new IllegalArgumentException("quartzScheduled.fixedRate() must be defined!");
        }
        IntervalProvider bean = getBean(quartzScheduled.qualifier(), intervalProviderClass);
        Duration interval = bean.getInterval();
        if (interval == null) {
            throw new TriggerBeanCreationException("Interval is null!", quartzScheduled);
        }
        SimpleTriggerFactoryBean simpleTriggerFactoryBean = new SimpleTriggerFactoryBean();
        simpleTriggerFactoryBean.setRepeatInterval(interval.get(SECONDS) * 1000);
        if (bean.getInitialDelay() != null) {
            simpleTriggerFactoryBean.setStartDelay(interval.get(SECONDS) * 1000);
        }
        return simpleTriggerFactoryBean;
    }

    private <T>  T getBean(String qualifier, Class<T> intervalProviderClass) {
        if (StringUtils.isEmpty(qualifier)) {
            return applicationContext.getBean(intervalProviderClass);
        } else {
            return applicationContext.getBean(qualifier, intervalProviderClass);
        }
    }


    public static class TriggerBeanCreationException extends BeanCreationException {
        private final QuartzScheduled quartzScheduledAnnotation;


        TriggerBeanCreationException(String message, Throwable exc, QuartzScheduled quartzScheduledAnnotation) {
            super(message, exc);
            this.quartzScheduledAnnotation = quartzScheduledAnnotation;
        }

        TriggerBeanCreationException(String message, QuartzScheduled quartzScheduledAnnotation) {
            super(message);
            this.quartzScheduledAnnotation = quartzScheduledAnnotation;
        }

        public QuartzScheduled getScheduledAnnotation() {
            return quartzScheduledAnnotation;
        }
    }

}
