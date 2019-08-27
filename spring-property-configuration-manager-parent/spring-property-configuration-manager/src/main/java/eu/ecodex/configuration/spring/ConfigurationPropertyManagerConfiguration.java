package eu.ecodex.configuration.spring;

import eu.ecodex.utils.configuration.service.ConfigurationPropertyManagerImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConfigurationPropertyManagerConfiguration {


    @Bean
    public ConfigurationPropertyManagerImpl configurationPropertyManagerImpl() {
        return new ConfigurationPropertyManagerImpl();
    }


}
