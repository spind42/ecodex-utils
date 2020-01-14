package eu.ecodex.utils.configuration.ui.vaadin.tools;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.*;
import com.vaadin.flow.data.converter.Converter;
import com.vaadin.flow.shared.Registration;
import eu.ecodex.utils.configuration.domain.ConfigurationProperty;
import eu.ecodex.utils.configuration.service.ConfigurationPropertyCollector;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.validator.internal.metadata.descriptor.ConstraintDescriptorImpl;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

import javax.validation.ConstraintViolation;
import javax.validation.ElementKind;
import javax.validation.Path;
import javax.validation.metadata.ConstraintDescriptor;
import java.lang.annotation.ElementType;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;


@Service
//@UIScope
public class ConfigurationFormFactory {

    private static final Logger LOGGER = LogManager.getLogger(ConfigurationFormFactory.class);

    @Autowired
    ConfigurationPropertyCollector configurationPropertyCollector;

    @Autowired(required = false)
    List<ConfigurationFieldFactory> fieldCreatorFactories = new ArrayList<>();

    @Autowired
    javax.validation.Validator validator;

    @Autowired
    ConversionService conversionService;


    public ConfigurationPropertyForm createFormFromConfigurationPropertiesClass(Class clazz) {
        if (!clazz.isAnnotationPresent(ConfigurationProperties.class)) {
            throw new IllegalArgumentException("the passed class must be annotated with " + ConfigurationProperties.class);
        }
        List<ConfigurationProperty> configurationPropertyFromClazz = configurationPropertyCollector.getConfigurationPropertyFromClazz(clazz);



        BeanValidationBinder binder = new BeanValidationBinder(clazz);


        binder.withValidator(new Validator<Object>() {

            @Override
            public ValidationResult apply(Object value, ValueContext context) {
                Set<ConstraintViolation<Object>> constraintViolationSet = validator.validate(value);
                if (constraintViolationSet.isEmpty()) {
                    return ValidationResult.ok();
                }
                String errors = constraintViolationSet
                        .stream()
                        .filter(constraintViolation -> {
//                            Iterator<Path.Node> iterator = constraintViolation.getPropertyPath().iterator();
//                            return ElementKind.PROPERTY == iterator.next().getKind();
//                            constraintViolation.getEl
                            ConstraintDescriptorImpl constraintDescriptor = (ConstraintDescriptorImpl) constraintViolation.getConstraintDescriptor();
                            return ElementType.TYPE == constraintDescriptor.getElementType();
                        } )
                        .map(constraintViolation -> constraintViolation.getMessage())

                        .collect(Collectors.joining("\n"));
                return ValidationResult.error(errors);
            }
        });

        ConfigurationPropertyForm formLayout = new ConfigurationPropertyForm(clazz, binder);

        configurationPropertyFromClazz.forEach(prop -> {
            String label = prop.getLabel();
            Component c = createComponentFromConfigurationProperty(binder, prop);


            formLayout.addFormItem(c, label);
        });


        try {
            formLayout.setValue(clazz.getDeclaredConstructor().newInstance());
        } catch (InstantiationException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        return formLayout;
    }

    public Component createComponentFromConfigurationProperty(BeanValidationBinder binder, ConfigurationProperty prop) {
        Optional<ConfigurationFieldFactory> ff = fieldCreatorFactories
                .stream()
                .filter(configurationFieldFactory -> configurationFieldFactory.canHandle(prop.getType()))
                .findFirst();
        if (ff.isPresent()) {
            ConfigurationFieldFactory configurationFieldFactory = ff.get();
            AbstractField field = configurationFieldFactory.createField(prop, binder);

//            configurationFieldFactory.bind(prop, binder, field);
            return field;
        } else {
            //Just create a simple text field...
            AbstractField field = createField(prop, binder);
            return field;
        }
    }


    private AbstractField createField(ConfigurationProperty configurationProperty, Binder binder) {
//        Class type = configurationProperty.getType();
        TextField tf = new TextField();

        Class parentClass = configurationProperty.getParentClass();

        binder.forField(tf)
                .withValidator(new Validator<Object>() {

                    @Override
                    public ValidationResult apply(Object value, ValueContext context) {
                        Object convertedValue = conversionService.convert(value, configurationProperty.getType());

                        Set<ConstraintViolation<?>> constraintViolationSet = validator.validateValue(parentClass, configurationProperty.getBeanPropertyName(), convertedValue);
                        if (constraintViolationSet.isEmpty()) {
                            return ValidationResult.ok();
                        }
                        String errors = constraintViolationSet.stream().map(constraintViolation -> constraintViolation.getMessage()).collect(Collectors.joining("\n"));
                        return ValidationResult.error(errors);
                    }
                })
                .withConverter(new Converter() {
                    @Override
                    public Result convertToModel(Object value, ValueContext context) {
                        if (value != null && "".equals(value.toString())){
                            return Result.ok(null);
                        }
                        if (conversionService.canConvert(value.getClass(), configurationProperty.getType())) {
                            return Result.ok(conversionService.convert(value, configurationProperty.getType()));
                        } else {
                            return Result.error("Cannot convert " + value.getClass() + " to " + configurationProperty.getType());
                        }
                    }

                    @Override
                    public Object convertToPresentation(Object value, ValueContext context) {
                        String convert = conversionService.convert(value, String.class);
//                        if ("".equals(convert)) {
//                            return null;
//                        }

                        return convert;
                    }
                })
                .withNullRepresentation("")
                .bind(configurationProperty.getBeanPropertyName());

        return tf;
    }


    public class ConfigurationPropertyForm extends FormLayout implements HasValue {

        private Label formStatusLabel = new Label();

        /**
         * The by the factory generated binder
         */
        private final Binder binder;
        /**
         * The specific type of the with @ConfigurationProperties
         * annotated class
         */
        private final Class clazz;
        private boolean readOnly;
//        private Object value;

        private ConfigurationPropertyForm(Class clazz, Binder binder) {
            this.binder = binder;
            this.clazz = clazz;
            BinderValidationStatusHandler defaultHandler = binder
                    .getValidationStatusHandler();

//            binder.setStatusLabel(formStatusLabel);

            //see: https://vaadin.com/docs/v10/flow/binding-data/tutorial-flow-components-binder-beans.html
            binder.setValidationStatusHandler(status -> {
                LOGGER.info("Binder validation status handler called: [{}]", status);
                // create an error message on failed bean level validations
                List<ValidationResult> errors = status
                        .getBeanValidationErrors();

                // collect all bean level error messages into a single string,
                // separating each message with a <br> tag
                String errorMessage = errors.stream()
                        .map(ValidationResult::getErrorMessage)
                        // sanitize the individual error strings to avoid code
                        // injection
                        // since we are displaying the resulting string as HTML
                        .map(errorString -> Jsoup.clean(errorString,
                                Whitelist.simpleText()))
                        .collect(Collectors.joining("<br>"));

                // finally, display all bean level validation errors in a single
                // label
                formStatusLabel.getElement().setProperty("innerHTML", errorMessage);
//                formStatusLabel.setVisible(!errorMessage.isEmpty());
//                setVisible(formStatusLabel, !errorMessage.isEmpty());

                // Let the default handler show messages for each field
                defaultHandler.statusChange(status);
            });
            this.addComponentAsFirst(formStatusLabel);
            formStatusLabel.setVisible(true);
        }

        @Override
        public void setValue(Object o) {
            if (!this.clazz.isAssignableFrom(o.getClass())) {
                throw new IllegalArgumentException("The passed object must be a subtype of " + this.clazz);
            }
            this.binder.setBean(o);
        }

        @Override
        public Object getValue() {
            return this.binder.getBean();
        }

        @Override
        public Registration addValueChangeListener(ValueChangeListener valueChangeListener) {
//            return binder.addValueChangeListener(valueChangeListener);
//            return null;
            return null;
        }

        @Override
        public void setReadOnly(boolean b) {
            this.readOnly = b;
        }

        @Override
        public boolean isReadOnly() {
            return readOnly;
        }

        @Override
        public void setRequiredIndicatorVisible(boolean b) {

        }

        @Override
        public boolean isRequiredIndicatorVisible() {
            return false;
        }

        public Binder getBinder() {
            return binder;
        }
    }

}
