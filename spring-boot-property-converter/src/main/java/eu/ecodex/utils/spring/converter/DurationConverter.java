package eu.ecodex.utils.spring.converter;

import org.springframework.core.convert.converter.Converter;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Converts from a String to Duration
 * by using the following pattern: {@literal ^(?<number>\d+)(?<unit>(ms)|([s,m,h,d]))$}
 *
 */
public class DurationConverter implements Converter<String, Duration> {


    private static final String PATTERN = "^(?<number>\\d+)(?<unit>(ms)|([s,m,h,d]))$";
    private static final Map<String, TemporalUnit> UNIT_MAP = Stream.of(
                new Object[] {"ms", ChronoUnit.MILLIS},
                new Object[] {"s", ChronoUnit.SECONDS},
                new Object[] {"m", ChronoUnit.MINUTES},
                new Object[] {"h", ChronoUnit.HOURS},
                new Object[] {"d", ChronoUnit.DAYS}
            )
            .collect(Collectors.toMap(objects -> {
                return (String) objects[0];
            }, objects  -> {
                return (TemporalUnit) objects[1];
            }));

    private final Pattern compiledPattern;


    public DurationConverter() {
        this.compiledPattern = Pattern.compile(PATTERN);
    }

    @Override
    public Duration convert(String source) {
        Matcher matcher = compiledPattern.matcher(source);
        int groupCount = matcher.groupCount();
        if (matcher.matches()) {
            String number = matcher.group("number");
            Long value = Long.parseLong(number);
            String unit = matcher.group("unit");
            TemporalUnit temporalUnit = UNIT_MAP.get(unit);
            return Duration.of(value, temporalUnit);
        }
        return Duration.parse(source);
    }

}
