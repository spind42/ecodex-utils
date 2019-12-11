package eu.ecodex.utils.configuration.service;

import eu.ecodex.configuration.spring.EnablePropertyConfigurationManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.source.ConfigurationPropertySource;
import org.springframework.boot.context.properties.source.MapConfigurationPropertySource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.PropertySource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@EnablePropertyConfigurationManager
class ConfigurationPropertyCheckerImplTest {

    private static final Logger LOGGER = LogManager.getLogger(ConfigurationPropertyCheckerImplTest.class);


//    @Autowired
//    private ConfigurationPropertyCollectorImpl configurationPropertyManager;

    @Autowired
    private ConfigurationPropertyChecker configPropertyChecker;

    private Properties getExampleProperties1() {
        Properties properties = new Properties();
        properties.put("example.configuration.text", "text");
        properties.put("example.configuration.number", "59");
        properties.put("example.abc.address", "Testgasse 2");

        return properties;

    }

//    @BeforeEach
//    public void getConfigPropertyChecker() {
//        this.configPropertyChecker = configurationPropertyManager.getConfigChecker();
//    }

    @Test
    void getStringValueForProperty() {

    }

    @Test
    void getValueForProperty() {
    }

    @Test
    void isConfigurationValid() {
    }

    @Test
    void isConfigurationValid1() {
    }


    @Test
    void isValid() {
        ConfigurationPropertySource configurationPropertySource = new MapConfigurationPropertySource(getExampleProperties1());
        configPropertyChecker.isConfigurationValid(configurationPropertySource, "eu.ecodex.utils.configuration.testdata");

    }

    @Test
    void isValid_shouldThrow() {


        Properties properties = getExampleProperties1();
        properties.put("example.configuration.number", "89"); //number is too big MAX(60)

        ConfigurationPropertySource configurationPropertySource = new MapConfigurationPropertySource(properties);



        Assertions.assertThrows( org.springframework.boot.context.properties.bind.BindException.class, () -> {
            try {
                configPropertyChecker.isConfigurationValid(configurationPropertySource, "eu.ecodex.utils.configuration.testdata");
            } catch (org.springframework.boot.context.properties.bind.BindException be) {
                LOGGER.info("Log bind Exception:", be);
//                LOGGER.info("origin: [{}], Property: [{}], message: [{}] ", be.getProperty().getOrigin(), be.getMessage());
                throw be;
            }
        });
    }

}