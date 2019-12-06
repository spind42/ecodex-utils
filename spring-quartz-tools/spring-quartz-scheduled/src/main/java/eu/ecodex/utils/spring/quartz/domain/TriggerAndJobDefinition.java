package eu.ecodex.utils.spring.quartz.domain;

import org.springframework.core.style.ToStringCreator;
import org.springframework.scheduling.annotation.Scheduled;

import java.lang.reflect.Method;

public class TriggerAndJobDefinition {

    private final String beanName;
    Scheduled scheduled;
    Method method;
    Object bean;

    public TriggerAndJobDefinition(String beanName, Scheduled scheduled, Method method, Object bean) {
        this.beanName = beanName;
        this.scheduled = scheduled;
        this.method = method;
        this.bean = bean;
    }

    public String getBeanName() {
        return beanName;
    }

    public Scheduled getScheduled() {
        return scheduled;
    }

    public void setScheduled(Scheduled scheduled) {
        this.scheduled = scheduled;
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
