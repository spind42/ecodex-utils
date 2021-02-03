package eu.ecodex.utils.spring.quartz.annotation;


import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface QuartzSchedules {

    QuartzScheduled[] value();

}
