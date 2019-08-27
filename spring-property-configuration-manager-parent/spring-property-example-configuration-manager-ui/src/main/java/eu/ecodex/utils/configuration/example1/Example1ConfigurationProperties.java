package eu.ecodex.utils.configuration.example1;

import eu.ecodex.utils.configuration.api.annotation.ConfigurationDescription;
import eu.ecodex.utils.configuration.api.annotation.ConfigurationLabel;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "com.example1")
public class Example1ConfigurationProperties {

    @ConfigurationLabel("Property1")
    @ConfigurationDescription("I am a good long description of this property!")
    String property1;


    public String getProperty1() {
        return property1;
    }

    public void setProperty1(String property1) {
        this.property1 = property1;
    }
}
