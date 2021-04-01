package eu.ecodex.utils.spring.quartz.configuration;

import com.mchange.v1.util.CollectionUtils;
import eu.ecodex.utils.spring.quartz.domain.TriggerAndJobDefinition;
import org.quartz.Scheduler;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.quartz.QuartzAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Role;
import org.springframework.scheduling.config.TaskManagementConfigUtils;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Configuration
@ConditionalOnClass({ Scheduler.class, SchedulerFactoryBean.class, PlatformTransactionManager.class })
@AutoConfigureAfter(QuartzAutoConfiguration.class)
@ImportAutoConfiguration(QuartzAutoConfiguration.class)
public class ScheduledWithQuartzConfiguration {

    public static final String TRIGGER_AND_JOB_DEFINITION_LIST_BEAN_NAME = "eu.ecodex.utils.spring.quartz.configuration.TriggerAndJobDefinitionListBean";

    @Bean(name = TRIGGER_AND_JOB_DEFINITION_LIST_BEAN_NAME)
    List<TriggerAndJobDefinition> triggerAndJobDefinitionList(){
        return new CopyOnWriteArrayList<>();
    }

    @Bean
    @ConditionalOnMissingBean(name = TaskManagementConfigUtils.SCHEDULED_ANNOTATION_PROCESSOR_BEAN_NAME)
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    ScheduledBeanPostProcessor scheduledBeanPostProcessor() {
        return new ScheduledBeanPostProcessor();
    }

    @Bean
    TriggerAndJobDefinitionProcessor triggerAndJobDefinition() {
        return new TriggerAndJobDefinitionProcessor();
    }


}
