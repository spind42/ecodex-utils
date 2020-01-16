package eu.ecodex.utils.configuration.service;

import eu.ecodex.configuration.spring.EnablePropertyConfigurationManager;
import eu.ecodex.utils.configuration.domain.ConfigurationProperty;
import eu.ecodex.utils.configuration.testdata.ConfigurationPackageTestdata;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@EnablePropertyConfigurationManager
class ConfigurationPropertyCollectorImplTest {

    private static final Logger LOGGER = LogManager.getLogger(ConfigurationPropertyCollectorImplTest.class);



    @Autowired
    private ConfigurationPropertyCollectorImpl configurationPropertyManager;

    @Test
    void test_getAll_byClassName() {
        Collection<ConfigurationProperty> all = configurationPropertyManager.getConfigurationProperties(ConfigurationPackageTestdata.class);

        LOGGER.info("all config properties are: [{}]", all);

        Assertions.assertAll(
                () -> Assertions.assertNotNull(all),
                () -> assertThat(all).hasSize(4)
        );
    }


    @Test
    void getAll() {

        Collection<ConfigurationProperty> all = configurationPropertyManager.getConfigurationProperties("eu.ecodex.utils.configuration.testdata");

        LOGGER.info("all config properties are: [{}]", all);

        Assertions.assertAll(
                () -> Assertions.assertNotNull(all),
                () -> assertThat(all).hasSize(4)
        );
    }

    @Test
    void getAll_withSubpackageFiltering() {

        Collection<ConfigurationProperty> all = configurationPropertyManager.getConfigurationProperties("eu.ecodex.utils.configuration.testdata.subpackage1");

        LOGGER.info("all config properties are: [{}]", all);

        Assertions.assertAll(
                () -> Assertions.assertNotNull(all),
                () -> assertThat(all).hasSize(2)
        );
    }


}