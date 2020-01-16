package eu.ecodex.utils.configuration.service;

import eu.ecodex.utils.configuration.api.annotation.ConfigurationDescription;
import eu.ecodex.utils.configuration.api.annotation.ConfigurationLabel;

import eu.ecodex.utils.configuration.domain.ConfigurationPropertiesBean;
import eu.ecodex.utils.configuration.domain.ConfigurationProperty;
import eu.ecodex.utils.configuration.domain.ConfigurationPropertyNode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.SpringProperties;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.StringUtils;
import org.springframework.validation.Validator;

import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class ConfigurationPropertyCollectorImpl implements ConfigurationPropertyCollector {

    private static final Logger LOGGER = LogManager.getLogger(ConfigurationPropertyCollector.class);

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private Validator validator;

    public ConfigurationPropertyChecker getConfigChecker() {
        return new ConfigurationPropertyCheckerImpl( this, validator);
    }

    /**
     * returns a list of all configuration properties
     * each ConfigurationProperty object holds the configuration property key, optional if set a description and label name
     * for further information see {@link ConfigurationProperty}
     *
     * @param basePackageFilter - the provided string is used as a filter, only with {@link ConfigurationProperties} annotated classes
     *                          under this package path are scanned and returned
     * @return a list of ConfigurationProperty objects
     */
    @Override
    public Collection<ConfigurationProperty> getConfigurationProperties(String... basePackageFilter) {
        return getConfigurationProperties(Arrays.asList(basePackageFilter));
    }

    @Override
    public ConfigurationPropertyNode getConfigurationPropertiesHirachie(String... basePackageFilter) {
        return getConfigurationPropertiesHirachie(Arrays.asList(basePackageFilter));
    }

    private ConfigurationPropertyNode getConfigurationPropertiesHirachie(List<String> basePackageFilter) {
        Map<String, ConfigurationProperty> configurationPropertiesMap = getConfigurationPropertiesMap(basePackageFilter);

        ConfigurationPropertyNode rootNode = new ConfigurationPropertyNode();


        configurationPropertiesMap.values()
                .stream()

//                .sorted(Comparator.comparingInt(p -> StringUtils.countOccurrencesOf(p.getPropertyName(), ".")))

                .forEach( prop -> {
                    String propName = prop.getPropertyName();
                    ConfigurationPropertyNode currentNode = rootNode;

                    String[] split = StringUtils.split(propName, ".");
                    while (split != null) {
                        String beforeDot = split[0];
                        propName = split[1];
                        Optional<ConfigurationPropertyNode> node = currentNode.getChild(beforeDot);
                        if (node.isPresent()) {
                            currentNode = node.get();
                        } else {
                            ConfigurationPropertyNode newNode = new ConfigurationPropertyNode();
                            newNode.setNodeName(beforeDot);
                            currentNode.addChild(newNode);
                            currentNode = newNode;
                        }
                        split = StringUtils.split(propName, ".");
                    }
                    //set the property on the last node
                    ConfigurationPropertyNode newNode = new ConfigurationPropertyNode();
                    newNode.setNodeName(propName);
                    newNode.setProperty(prop);
                    currentNode.addChild(newNode);

                });
        return rootNode;

    }

    @Override
    public Collection<ConfigurationProperty> getConfigurationProperties(Class... basePackageClasses) {
        List<String> collect = Stream.of(basePackageClasses)
                .map(basePackageClass -> basePackageClass.getPackage().getName())
                .collect(Collectors.toList());

        return this.getConfigurationProperties(collect);
    }

    private Collection<ConfigurationProperty> getConfigurationProperties(List<String> basePackageFilter) {
        return getConfigurationPropertiesMap(basePackageFilter).values();
    }

    private Map<String, ConfigurationProperty> getConfigurationPropertiesMap(List<String> basePackageFilter) {
        List<ConfigurationPropertiesBean> configurationBeans = this.getConfigurationBeans(basePackageFilter);

        Map<String, ConfigurationProperty> collect = configurationBeans
                .stream()
                //filter out classes which aren't in package path basePackageFilter
//                .filter(new PackageFilter(basePackageFilter))
                .map(this::processBean)
                .map(List::stream)
                .flatMap(Function.identity())
                .collect(Collectors.toMap(c -> c.getPropertyName(), c -> c));

        return collect;
    }


    private List<ConfigurationProperty> processBean(ConfigurationPropertiesBean entry) {
        LOGGER.trace("processing config bean with name: [{}]", entry.getBeanName());
        Object bean = entry.getBean();
        Class<?> beanClazz = bean.getClass();

//        if (!beanClass.getPackage().getName().startsWith(basePackageFilter)) {
//            LOGGER.debug("ignore bean [{}] because its not in the scanning package path [{}]", beanClass, basePackageFilter);
//            return Stream.empty();
//        }
        return getConfigurationPropertyFromClazz(beanClazz);

    }

    @Override
    public List<ConfigurationProperty> getConfigurationPropertyFromClazz(Class<?> beanClass) {
        if (!beanClass.isAnnotationPresent(ConfigurationProperties.class)) {
            throw new IllegalArgumentException("Class must be annotated with " + ConfigurationProperties.class);
        }
        ConfigurationProperties configurationProperties = beanClass.getAnnotation(ConfigurationProperties.class);
        String pPrefix = configurationProperties.prefix();
        if (pPrefix.length() > 0) {
            pPrefix = pPrefix + ".";
        }
        final String propertyPrefix = pPrefix;


        Field[] fields = beanClass.getDeclaredFields(); //TODO: also scan inherited fields...
        return Stream.of(fields)
                .map(this::processFieldOfBean)
                .map(c -> {
                    c.setPropertyName(propertyPrefix + c.getPropertyName());
                    c.setParentClass(beanClass);
                    return c;
                }).collect(Collectors.toList())
                ;
    }

    private ConfigurationProperty processFieldOfBean(Field field) {
        LOGGER.trace("processing field [{}]", field);
        ConfigurationProperty c = new ConfigurationProperty();
        c.setPropertyName(field.getName());
        c.setBeanPropertyName(field.getName());

        ConfigurationDescription descriptionAnnotation = AnnotationUtils.getAnnotation(field, ConfigurationDescription.class);
        if (descriptionAnnotation != null) {
            String description = (String) AnnotationUtils.getValue(descriptionAnnotation, "description");
            c.setDescription(description);
        }

        ConfigurationLabel configLabelAnnotation = AnnotationUtils.getAnnotation(field, ConfigurationLabel.class);
        if (configLabelAnnotation != null) {
            String label = (String) AnnotationUtils.getValue(configLabelAnnotation);
            c.setLabel(label);
        }

        c.setType(field.getType());

        return c;
    }

    @Override
    public List<ConfigurationPropertiesBean> getConfigurationBeans(List<String> basePackageFilter) {
        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AnnotationTypeFilter(ConfigurationProperties.class));

        //all @ConfigurationProperties bean definitions
        List<BeanDefinition> collect = basePackageFilter.stream().map(basePackage -> scanner.findCandidateComponents(basePackage))
                .flatMap(beanDefinitions -> beanDefinitions.stream())
                .collect(Collectors.toList());

        //TODO: check if active! @Profile + @ConditionalOnProperty



        Map<String, Object> configurationBeans = applicationContext.getBeansWithAnnotation(ConfigurationProperties.class);
        List<ConfigurationPropertiesBean> configurationBeansList = configurationBeans.entrySet().stream()
                .filter(new PackageFilter(basePackageFilter))
                .map(this::processConfigurationPropertiesBean)
                .collect(Collectors.toList());

        return configurationBeansList;
    }

    private ConfigurationPropertiesBean processConfigurationPropertiesBean(Map.Entry<String, Object> entry) {
        ConfigurationPropertiesBean c = new ConfigurationPropertiesBean();

        Object bean = entry.getValue();
        Class<?> beanClass = bean.getClass();

        c.setBeanName(entry.getKey());
        c.setBeanClazz(beanClass);
        c.setBean(bean);

        ConfigurationProperties configurationProperties = beanClass.getAnnotation(ConfigurationProperties.class);
        String pPrefix = configurationProperties.prefix();
        c.setPropertyPrefix(pPrefix);

        ConfigurationLabel configLabelAnnotation = beanClass.getAnnotation(ConfigurationLabel.class);
        if (configLabelAnnotation != null) {
            String label = (String) AnnotationUtils.getValue(configLabelAnnotation);
            c.setLabel(label);
        }

        ConfigurationDescription descriptionAnnotation = beanClass.getAnnotation(ConfigurationDescription.class);
        if (descriptionAnnotation != null) {
            String description = (String) AnnotationUtils.getValue(descriptionAnnotation);
            c.setDescription(description);
        }


        return c;
    }


}
