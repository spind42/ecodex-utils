package eu.ecodex.utils.configuration;

import eu.ecodex.utils.configuration.domain.ConfigurationProperty;
import eu.ecodex.utils.configuration.service.ConfigurationPropertyManagerImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.source.ConfigurationPropertySource;
import org.springframework.boot.context.properties.source.MapConfigurationPropertySource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
class ConfigurationPropertyManagerImplTest {

    private static final Logger LOGGER = LogManager.getLogger(ConfigurationPropertyManagerImplTest.class);



    @Autowired
    private ConfigurationPropertyManagerImpl configurationPropertyManager;

    @org.junit.jupiter.api.Test
    void getAll() {

        List<ConfigurationProperty> all = configurationPropertyManager.getAll("eu.ecodex.utils.configuration");

        LOGGER.info("all config properties are: [{}]", all);

        Assertions.assertAll(
                () -> Assertions.assertNotNull(all),
                () -> assertThat(all).hasSize(4)
        );
    }

    @org.junit.jupiter.api.Test
    void getAll_withSubpackageFiltering() {

        List<ConfigurationProperty> all = configurationPropertyManager.getAll("eu.ecodex.utils.configuration.testdata");

        LOGGER.info("all config properties are: [{}]", all);

        Assertions.assertAll(
                () -> Assertions.assertNotNull(all),
                () -> assertThat(all).hasSize(2)
        );
    }

    @Test
    void isValidTest() {

        Properties properties = new Properties();
        properties.put("example.configuration.text", "text");
        properties.put("example.configuration.number", "59");
        properties.put("example.abc.address", "Testgasse 2");

        ConfigurationPropertySource configurationPropertySource = new MapConfigurationPropertySource(properties);

        configurationPropertyManager.isConfigurationValid(configurationPropertySource, "eu.ecodex.utils.configuration");

    }

    @Test
    void isValidTest_shouldThrow() {

        Properties properties = new Properties();
        properties.put("example.configuration.text", "text");
        properties.put("example.configuration.number", "89"); //number is too big MAX(60)
        properties.put("example.abc.address", "Testgasse 2");

        ConfigurationPropertySource configurationPropertySource = new MapConfigurationPropertySource(properties);

        Assertions.assertThrows( org.springframework.boot.context.properties.bind.BindException.class, () -> {
            try {
                configurationPropertyManager.isConfigurationValid(configurationPropertySource, ConfigurationPackage.class);
            } catch (org.springframework.boot.context.properties.bind.BindException be) {
                LOGGER.info("Log bind Exception:", be);
//                LOGGER.info("origin: [{}], Property: [{}], message: [{}] ", be.getProperty().getOrigin(), be.getMessage());
                throw be;
            }
        });
    }

}