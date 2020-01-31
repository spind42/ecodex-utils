package eu.ecodex.utils.spring.converter;

import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class ConverterAutoConfiguration {

    @Bean
    @ConfigurationPropertiesBinding
    public PathConverter stringToPathConverter() {
        return new PathConverter();
    }

    @Bean
    @ConfigurationPropertiesBinding
    public DurationConverter stringToDurationConverter() {
        return new DurationConverter();
    }

    @Bean
    @ConfigurationPropertiesBinding
    public ResourceConverter stringToResourceConverter() {return new ResourceConverter(); }

}
