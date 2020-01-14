package eu.ecodex.utils.configuration.ui.vaadin.tools;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.*;
import com.vaadin.flow.data.converter.Converter;
import eu.ecodex.utils.configuration.domain.ConfigurationProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintViolation;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class NumberConfigurationField implements ConfigurationFieldFactory {


    @Autowired
    ConversionService conversionService;

    @Autowired
    private javax.validation.Validator validator;

    @Override
    public boolean canHandle(Class clazz) {
//        Optional<? extends Class<? extends Number>> any = Stream.of(Integer.class, Long.class).filter(clz -> clz == clazz).findAny();
//        return any.isPresent();
        return false;
    }

    @Override
    public AbstractField createField(ConfigurationProperty configurationProperty, Binder binder) {
////        Class type = configurationProperty.getType();
//        TextField tf = new TextField();
//
//        Class bindedClass = binder.getBean().getClass();
//
//        binder.forField(tf)
//                .withValidator(new Validator<Object>() {
//
//                    @Override
//                    public ValidationResult apply(Object value, ValueContext context) {
//                        Set<ConstraintViolation<?>> set = validator.validateValue(bindedClass, configurationProperty.getBeanPropertyName(), value);
//                        if (set.isEmpty()) {
//                            return ValidationResult.ok();
//                        }
//                        String errors = set.stream().map(constraintViolation -> constraintViolation.getMessage()).collect(Collectors.joining("\n"));
//                        return ValidationResult.error(errors);
//                    }
//                })
//                .withConverter(new Converter() {
//                    @Override
//                    public Result convertToModel(Object value, ValueContext context) {
//                        if (conversionService.canConvert(value.getClass(), configurationProperty.getType())) {
//                            return Result.ok(conversionService.convert(value, configurationProperty.getType()));
//                        } else {
//                            return Result.error("Cannot convert " + value.getClass() + " to " + configurationProperty.getType());
//                        }
//                    }
//
//                    @Override
//                    public Object convertToPresentation(Object value, ValueContext context) {
//                        return conversionService.convert(value, String.class);
//                    }
//                })
//                .bind(configurationProperty.getBeanPropertyName());
//
//        return tf;
        return null;
    }


}
