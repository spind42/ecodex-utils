package eu.ecodex.utils.configuration.ui.vaadin.tools.configfield;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.*;
import com.vaadin.flow.function.ValueProvider;
import eu.ecodex.utils.configuration.domain.ConfigurationProperty;
import eu.ecodex.utils.configuration.ui.vaadin.tools.ConfigurationFieldFactory;
import eu.ecodex.utils.configuration.ui.vaadin.tools.UiConfigurationConversationService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintViolation;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Order(Ordered.LOWEST_PRECEDENCE)
public class DefaultTextFieldFactory implements ConfigurationFieldFactory {

    @Autowired
    @UiConfigurationConversationService
    ConversionService conversionService;

    @Autowired
    javax.validation.Validator validator;

    @Override
    public boolean canHandle(Class clazz) {
        return conversionService.canConvert(String.class, clazz);
    }

    @Override
    public AbstractField createField(ConfigurationProperty configurationProperty, Binder binder) {
        TextField tf = new TextField();

        Class parentClass = configurationProperty.getParentClass();

        Binder.BindingBuilder<Properties, String> propertiesStringBindingBuilder = binder.forField(tf);
        if (parentClass != null) {
            //can currently only add a validator if the parent class is known and has Bean Validation
            propertiesStringBindingBuilder = propertiesStringBindingBuilder.withValidator(new Validator<String>() {

                @Override
                public ValidationResult apply(String value, ValueContext context) {


                    Object convertedValue = value;
                    try {
                        if (StringUtils.isNotEmpty(value)) {
                            convertedValue = conversionService.convert(value, configurationProperty.getType());
                        } else {
                            convertedValue = null;
                        }
                    } catch (ConversionFailedException conversionFailed) {
                        //TODO: improve error message...
                        return ValidationResult.error(conversionFailed.getMessage());
                    }

                    Set<ConstraintViolation<?>> constraintViolationSet = validator.validateValue(parentClass, configurationProperty.getBeanPropertyName(), convertedValue);
                    if (constraintViolationSet.isEmpty()) {
                        return ValidationResult.ok();
                    }
                    String errors = constraintViolationSet.stream()
                            .map(constraintViolation -> constraintViolation.getMessage())
                            .collect(Collectors.joining("\n"));
                    return ValidationResult.error(errors);
                }
            });
        }

        propertiesStringBindingBuilder.withNullRepresentation("");

        Binder.Binding<Properties, String> binding = propertiesStringBindingBuilder.bind(
                (ValueProvider<Properties, String>) o -> o.getProperty(configurationProperty.getPropertyName(), null),
                (Setter<Properties, String>) (props, value) -> {
                    if (value == null) {
                        props.remove(configurationProperty.getPropertyName());
                    } else {
                        props.put(configurationProperty.getPropertyName(), value);
                    }
                });
        return tf;
    }



}
