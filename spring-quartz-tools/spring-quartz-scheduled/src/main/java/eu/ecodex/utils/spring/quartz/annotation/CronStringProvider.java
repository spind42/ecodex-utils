package eu.ecodex.utils.spring.quartz.annotation;

import java.time.Duration;

public interface CronStringProvider {

    public String getCronString();

    default public String zone() {
        return "";
    }

    class DefaultCronStringProvider implements CronStringProvider {

        @Override
        public String getCronString() {
            return null;
        }
    }

}
