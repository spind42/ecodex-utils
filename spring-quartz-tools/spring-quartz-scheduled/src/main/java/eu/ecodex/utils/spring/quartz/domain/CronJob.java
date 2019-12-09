package eu.ecodex.utils.spring.quartz.domain;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.MDC;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.lang.reflect.Method;

//@DisallowConcurrentExecution
public class CronJob extends QuartzJobBean {

    public static final String MDC_ACTIVE_QUARTZ_JOB = "quartzJobName";

    private static final Logger LOGGER = LogManager.getLogger(QuartzJobBean.class);

    private volatile boolean toStopFlag = true;
    private Method method;
    private Object bean;

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

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {


        MDC.put(MDC_ACTIVE_QUARTZ_JOB, convertJobToGroupName(jobExecutionContext.getJobDetail()));
        try {
            LOGGER.trace("Invoking method [{}] on bean [{}] to run cron job", method, bean);
            method.invoke(bean);
        } catch (Exception e) {

            throw new JobExecutionException(e);
        } finally {
            MDC.remove(MDC_ACTIVE_QUARTZ_JOB);
        }
    }

    private String convertJobToGroupName(JobDetail jobDetail) {
        return jobDetail.getKey().getGroup() + "_" + jobDetail.getKey().getName();
    }

}
