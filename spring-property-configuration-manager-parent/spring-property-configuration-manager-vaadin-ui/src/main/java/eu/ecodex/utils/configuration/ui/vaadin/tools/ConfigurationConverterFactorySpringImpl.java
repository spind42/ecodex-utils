package eu.ecodex.utils.configuration.ui.vaadin.tools;

import com.vaadin.flow.data.converter.Converter;
import eu.ecodex.utils.configuration.domain.ConfigurationProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;
import org.springframework.context.ApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.core.ResolvableType;
import org.springframework.core.annotation.Order;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@Component
@Order(Ordered.LOWEST_PRECEDENCE)
public class ConfigurationConverterFactorySpringImpl implements ConfigurationConverterFactory {

//    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
//    @Qualifier(value = ConfigurationPropertiesBinding.VALUE)
//    @Autowired(required = false)
//    private List<org.springframework.core.convert.converter.Converter> springConverter = new ArrayList<>();
//
//    @Autowired
//    private ApplicationContext applicationContext;

    @Autowired
    ConversionService conversionService;

    @Override
    public boolean canConvert(Class clazz) {
        conversionService.canConvert(String.class, clazz);
//        ResolvableType resolvableType = ResolvableType.forClassWithGenerics(org.springframework.core.convert.converter.Converter.class, String.class, clazz);
//        String[] beanNamesForType = applicationContext.getBeanNamesForType(resolvableType);
//        Type t;
//        ResolvableType rt;
//        applicationContext.getBeanNamesForType()
//        springConverter.stream().filter(converter -> {
//
//        })
        return true;
    }

    @Override
    public Converter createConverter(ConfigurationProperty configurationProperty) {
        configurationProperty.getType();


        return null;
    }

}
