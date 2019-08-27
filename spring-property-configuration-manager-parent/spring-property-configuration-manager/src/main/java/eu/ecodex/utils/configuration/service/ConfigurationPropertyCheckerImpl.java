package eu.ecodex.utils.configuration.service;

import eu.ecodex.utils.configuration.domain.ConfigurationPropertiesBean;
import eu.ecodex.utils.configuration.domain.ConfigurationProperty;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.BindResult;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.bind.validation.ValidationBindHandler;
import org.springframework.boot.context.properties.source.ConfigurationPropertySource;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.validation.Validator;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ConfigurationPropertyCheckerImpl {

    private static final Logger LOGGER = LogManager.getLogger(ConfigurationPropertyCheckerImpl.class);

    private ConfigurationPropertySource configurationPropertySource;

    private Validator validator;

    private ConfigurationPropertyManager configurationPropertyManager;

    public String getStringValueForProperty(ConfigurationProperty configProperty) {
        return null;
    }

    public Object getValueForProperty(ConfigurationProperty configProperty) {
        return null;
    }


    public void isConfigurationValid(ConfigurationPropertySource configurationPropertySource, Class... basePackageFilter) {

        List<String> packageName = Arrays.asList(basePackageFilter)
                .stream()
                .map(Class::getPackage)
                .map(Package::getName)
                .collect(Collectors.toList());
        isConfigurationValid(configurationPropertySource, packageName);
    }



    /**
     * Tests if the configuration is valid, all properties are loaded from the
     * provided configuration source
     *
     * @param configurationPropertySource - the propertySources
     * @param basePackageFilter           - is only scanning with @ConfigurationProperties annotated classes under the specified package
     */
    public void isConfigurationValid(ConfigurationPropertySource configurationPropertySource, List<String> basePackageFilter) {
//        Map<String, Object> configurationBeans = applicationContext.getBeansWithAnnotation(ConfigurationProperties.class);

        List<ConfigurationPropertiesBean> configurationBeans = configurationPropertyManager.getConfigurationBeans(basePackageFilter);

        configurationBeans.stream()

                .forEach(entry -> {

                    Class<?> configClass = entry.getBeanClazz();
                    try {
                        Object config = configClass.getDeclaredConstructor().newInstance();
                        ConfigurationProperties annotation = AnnotationUtils.getAnnotation(configClass, ConfigurationProperties.class);
                        String prefix = (String) AnnotationUtils.getValue(annotation);

                        Bindable<?> bindable = Bindable.of(configClass).withAnnotations(annotation);
                        Binder b = new Binder(configurationPropertySource);
                        LOGGER.debug("Binding class [{}] with prefix [{}]", configClass, prefix);

                        ValidationBindHandler validationBindHandler = new ValidationBindHandler(validator);

                        BindResult<?> bind = b.bind(prefix, bindable, validationBindHandler);

                        if (bind.isBound()) {
                            LOGGER.trace("is bound!");
                        }
                        //TODO: validate bounded variables

                    } catch (InstantiationException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                });
    }


}
