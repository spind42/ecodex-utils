package eu.ecodex.utils.spring.quartz;

import eu.ecodex.utils.spring.quartz.configuration.ScheduledWithQuartzConfiguration;
import eu.ecodex.utils.spring.quartz.domain.TriggerAndJobDefinition;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ActiveProfiles;

import java.time.Duration;
import java.util.List;

import static eu.ecodex.utils.spring.quartz.configuration.ScheduledWithQuartzConfiguration.TRIGGER_AND_JOB_DEFINITION_LIST_BEAN_NAME;
import static org.assertj.core.api.Assertions.assertThat;

//@SpringBootTest(classes = QuartzScheduledJobTest.QuartzScheduledContext.class, properties = "debug=true")
@SpringBootTest(properties = "debug=true")
@ActiveProfiles("test")
public class QuartzScheduledJobTest {

    static ConfigurableApplicationContext APP_CONTEXT;

    @SpringBootApplication(scanBasePackages = "eu.ecodex.utils.spring.quartz")
    static class QuartzScheduledContext {

    }


//    @BeforeAll
//    public static void beforeAll() {
//        SpringApplicationBuilder builder = new SpringApplicationBuilder();
//        APP_CONTEXT = builder
//                .sources(QuartzScheduledContext.class)
//                .web(WebApplicationType.NONE)
//                .properties("debug=true")
//                .run();
//    }

//    @BeforeEach
//    public void beforeEach() {
//        APP_CONTEXT.getBeanFactory().autowireBean(this);
//    }

    @Autowired(required = false)
    @Qualifier(TRIGGER_AND_JOB_DEFINITION_LIST_BEAN_NAME)
    private List<TriggerAndJobDefinition> triggerAndJobDefinitionList;

    @Test
    public void testRegisteredScheduled() {
//        List triggerAndJobDefinitionList = (List) APP_CONTEXT.getBean(TRIGGER_AND_JOB_DEFINITION_LIST_BEAN_NAME);
        assertThat(triggerAndJobDefinitionList).hasSize(1);
    }

    @Test
    public void testJobsRunning() throws InterruptedException {
//        List triggerAndJobDefinitionList = (List) APP_CONTEXT.getBean(TRIGGER_AND_JOB_DEFINITION_LIST_BEAN_NAME);
//        assertThat(triggerAndJobDefinitionList).hasSize(1);
        Thread.sleep(Duration.ofSeconds(30).toMillis());
    }

    @Test
    public void testFixedDelay() {
        Assertions.fail("not implemented yet!");
    }


}
