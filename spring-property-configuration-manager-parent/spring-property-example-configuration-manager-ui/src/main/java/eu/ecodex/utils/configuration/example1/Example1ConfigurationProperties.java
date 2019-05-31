package eu.ecodex.utils.configuration.example1;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "com.example1")
public class Example1ConfigurationProperties {
}
