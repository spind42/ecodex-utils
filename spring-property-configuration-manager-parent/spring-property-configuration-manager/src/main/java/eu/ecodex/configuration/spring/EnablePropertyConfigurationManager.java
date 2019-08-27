package eu.ecodex.configuration.spring;

import eu.ecodex.utils.configuration.service.ConfigurationPropertyManagerImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import(ConfigurationPropertyManagerConfiguration.class)
public @interface EnablePropertyConfigurationManager {


}
