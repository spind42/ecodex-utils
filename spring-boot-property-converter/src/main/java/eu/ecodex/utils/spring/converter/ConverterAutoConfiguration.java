package eu.ecodex.utils.spring.converter;

import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConverterAutoConfiguration {

    @Bean
    @ConfigurationPropertiesBinding
    public PathConverter stringToPathConverter() {
        return new PathConverter();
    }

}
