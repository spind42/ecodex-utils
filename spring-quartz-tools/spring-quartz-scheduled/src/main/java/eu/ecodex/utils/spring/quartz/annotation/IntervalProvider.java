package eu.ecodex.utils.spring.quartz.annotation;

import java.time.Duration;

public interface IntervalProvider {

    public Duration getInterval();

    default public Duration getInitialDelay() {
        return Duration.ZERO;
    }

    class DefaultIntervalProvider implements IntervalProvider {

        @Override
        public Duration getInterval() {
            return null;
        }
    }

}
