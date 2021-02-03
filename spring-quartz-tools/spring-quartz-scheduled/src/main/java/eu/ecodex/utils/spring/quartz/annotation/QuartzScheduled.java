package eu.ecodex.utils.spring.quartz.annotation;


import java.lang.annotation.*;

import static eu.ecodex.utils.spring.quartz.annotation.IntervalProvider.*;

@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(QuartzSchedules.class)
@Documented
public @interface QuartzScheduled {

//    Class<? extends IntervalProvider> fixedDelay() default DefaultIntervalProvider.class;

    Class<? extends IntervalProvider> fixedRate()  default DefaultIntervalProvider.class;

    Class<? extends CronStringProvider> cron() default CronStringProvider.DefaultCronStringProvider.class;

    String qualifier() default "";

}
