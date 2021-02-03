package eu.ecodex.utils.spring.quartz.domain;

import eu.ecodex.utils.spring.quartz.annotation.QuartzScheduled;
import org.springframework.core.style.ToStringCreator;


import java.lang.reflect.Method;

public class TriggerAndJobDefinition {

    private final String beanName;
    QuartzScheduled quartzScheduled;
    Method method;
    Object bean;

    public TriggerAndJobDefinition(String beanName, QuartzScheduled quartzScheduled, Method method, Object bean) {
        this.beanName = beanName;
        this.quartzScheduled = quartzScheduled;
        this.method = method;
        this.bean = bean;
    }

    public String getBeanName() {
        return beanName;
    }

    public QuartzScheduled getScheduled() {
        return quartzScheduled;
    }

    public void setScheduled(QuartzScheduled quartzScheduled) {
        this.quartzScheduled = quartzScheduled;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public Object getBean() {
        return bean;
    }

    public void setBean(Object bean) {
        this.bean = bean;
    }

    public String toString() {
        return new ToStringCreator(this)
                .append("bean", this.beanName)
                .append("method", this.method.getName())
                .toString();
    }
}
