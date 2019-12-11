package eu.ecodex.utils.configuration.service;


import eu.ecodex.utils.configuration.domain.ConfigurationPropertiesBean;
import eu.ecodex.utils.configuration.domain.ConfigurationProperty;
import org.springframework.boot.context.properties.source.ConfigurationPropertySource;

import java.util.List;

public interface ConfigurationPropertyCollector {

    /**
     * Returns a list of all Properties (within with {@link org.springframework.boot.context.properties.ConfigurationProperties} annotated Classes)
     * in the provided basePackage path
     * @param basePackage
     * @return the list of Properties
     */
    List<ConfigurationProperty> getConfigurationProperties(String... basePackage);


    List<ConfigurationProperty> getConfigurationProperties(Class... basePackageClasses);


    List<ConfigurationPropertiesBean> getConfigurationBeans(List<String> basePackageFilter);
}
