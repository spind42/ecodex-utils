package eu.ecodex.utils.configuration;

import eu.ecodex.configuration.spring.EnablePropertyConfigurationManager;
import eu.ecodex.utils.configuration.domain.ConfigurationProperty;
import eu.ecodex.utils.configuration.service.ConfigurationPropertyManagerImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
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
@EnablePropertyConfigurationManager
class ConfigurationPropertyManagerImplTest {

    private static final Logger LOGGER = LogManager.getLogger(ConfigurationPropertyManagerImplTest.class);



    @Autowired
    private ConfigurationPropertyManagerImpl configurationPropertyManager;

    @Test
    void test_getAll_byClassName() {
        List<ConfigurationProperty> all = configurationPropertyManager.getConfigurationProperties(ConfigurationPackage.class);

        LOGGER.info("all config properties are: [{}]", all);

        Assertions.assertAll(
                () -> Assertions.assertNotNull(all),
                () -> assertThat(all).hasSize(4)
        );
    }


    @Test
    void getAll() {

        List<ConfigurationProperty> all = configurationPropertyManager.getConfigurationProperties("eu.ecodex.utils.configuration");

        LOGGER.info("all config properties are: [{}]", all);

        Assertions.assertAll(
                () -> Assertions.assertNotNull(all),
                () -> assertThat(all).hasSize(4)
        );
    }

    @Test
    void getAll_withSubpackageFiltering() {

        List<ConfigurationProperty> all = configurationPropertyManager.getConfigurationProperties("eu.ecodex.utils.configuration.testdata");

        LOGGER.info("all config properties are: [{}]", all);

        Assertions.assertAll(
                () -> Assertions.assertNotNull(all),
                () -> assertThat(all).hasSize(2)
        );
    }


}