package eu.ecodex.configuration.spring;

import eu.ecodex.utils.configuration.service.ConfigurationPropertyChecker;
import eu.ecodex.utils.configuration.service.ConfigurationPropertyCheckerImpl;
import eu.ecodex.utils.configuration.service.ConfigurationPropertyManagerImpl;
import org.springframework.boot.context.properties.source.ConfigurationPropertySource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConfigurationPropertyManagerConfiguration {


    @Bean
    public ConfigurationPropertyManagerImpl configurationPropertyManagerImpl() {
        return new ConfigurationPropertyManagerImpl();
    }

    @Bean
    public ConfigurationPropertyChecker configurationPropertyChecker() {
        return new ConfigurationPropertyCheckerImpl();
    }


}
