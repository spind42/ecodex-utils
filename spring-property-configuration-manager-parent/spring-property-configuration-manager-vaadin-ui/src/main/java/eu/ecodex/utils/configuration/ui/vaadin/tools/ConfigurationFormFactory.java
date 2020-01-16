package eu.ecodex.utils.configuration.ui.vaadin.tools;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.*;
import com.vaadin.flow.data.converter.Converter;
import com.vaadin.flow.function.ValueProvider;
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
import org.springframework.core.convert.ConversionFailedException;
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
        Collection<ConfigurationProperty> configurationPropertyFromClazz = configurationPropertyCollector.getConfigurationPropertyFromClazz(clazz);

//        BeanValidationBinder binder = new BeanValidationBinder(clazz);
        Binder<Properties> binder = new Binder<>(Properties.class);

//        binder.withValidator(new Validator<Properties>() {
//
//            @Override
//            public ValidationResult apply(Properties value, ValueContext context) {
//                Set<ConstraintViolation<Object>> constraintViolationSet = validator.validate(value);
//                if (constraintViolationSet.isEmpty()) {
//                    return ValidationResult.ok();
//                }
//                String errors = constraintViolationSet
//                        .stream()
//                        .filter(constraintViolation -> {
//
//                            ConstraintDescriptorImpl constraintDescriptor = (ConstraintDescriptorImpl) constraintViolation.getConstraintDescriptor();
//                            return ElementType.TYPE == constraintDescriptor.getElementType();
//                        } )
//                        .map(constraintViolation -> constraintViolation.getMessage())
//
//                        .collect(Collectors.joining("\n"));
//                return ValidationResult.error(errors);
//            }
//        });

        ConfigurationPropertyForm formLayout = new ConfigurationPropertyForm(clazz, binder);

        configurationPropertyFromClazz.forEach(prop -> {
//            String label = prop.getLabel();
            Component c = createComponentFromConfigurationProperty(binder, prop);
            formLayout.add(c);
//            formLayout.addFormItem(c);
        });

        formLayout.setValue(new Properties()); //setting empty properties...
//        try {
//            formLayout.setValue(clazz.getDeclaredConstructor().newInstance());
//        } catch (InstantiationException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
//            throw new RuntimeException(e);
//        }

        return formLayout;
    }

    public Component createComponentFromConfigurationProperty(Binder<Properties> binder, ConfigurationProperty prop) {
        HorizontalLayout horizontalLayout = new HorizontalLayout();
        Optional<ConfigurationFieldFactory> ff = fieldCreatorFactories
                .stream()
                .filter(configurationFieldFactory -> configurationFieldFactory.canHandle(prop.getType()))
                .findFirst();
        AbstractField field;
        if (ff.isPresent()) {
            ConfigurationFieldFactory configurationFieldFactory = ff.get();
            field = configurationFieldFactory.createField(prop, binder);

        } else {
            //Just create a simple text field...
            field = createField(prop, binder);
        }
        field.setId(prop.getPropertyName());

        Label label = new Label();

        label.setFor(field);
        label.setText(prop.getBeanPropertyName());
        horizontalLayout.add(label);

        horizontalLayout.add(field);

        Button infoButton = new Button();
        infoButton.setIcon(new Icon(VaadinIcon.INFO));
        infoButton.addClickListener(clickEvent -> {
            //TODO: show info box about property...
        });
        horizontalLayout.add(infoButton);

        return horizontalLayout;
    }


    public AbstractField createField(final ConfigurationProperty configurationProperty, final Binder<Properties> binder) {
//        Class type = configurationProperty.getType();
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
                        if (value != null) {
                            convertedValue = conversionService.convert(value, configurationProperty.getType());
                        }
                    } catch (ConversionFailedException conversionFailed) {
                        //TODO: improve error message...
                        return ValidationResult.error(conversionFailed.getMessage());
                    }

                    Set<ConstraintViolation<?>> constraintViolationSet = validator.validateValue(parentClass, configurationProperty.getBeanPropertyName(), convertedValue);
                    if (constraintViolationSet.isEmpty()) {
                        return ValidationResult.ok();
                    }
                    String errors = constraintViolationSet.stream().map(constraintViolation -> constraintViolation.getMessage()).collect(Collectors.joining("\n"));
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
                }
                props.put(configurationProperty.getPropertyName(), value);
            });


        return tf;
    }


    public class ConfigurationPropertyForm extends FormLayout implements HasValue<HasValue.ValueChangeEvent<Properties>, Properties> {

        private Label formStatusLabel = new Label();

        /**
         * The by the factory generated binder
         */
        private final Binder<Properties> binder;
        /**
         * The specific type of the with @ConfigurationProperties
         * annotated class
         */
        private final Class clazz;
        private boolean readOnly;
        private Properties properties;
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

//        @Override
//        public void setValue(Object o) {
//            if (!this.clazz.isAssignableFrom(o.getClass())) {
//                throw new IllegalArgumentException("The passed object must be a subtype of " + this.clazz);
//            }
//            this.binder.setBean(o);
//        }

        @Override
        public void setValue(Properties value) {
            this.properties = value;
            this.binder.setBean(value);
        }

        @Override
        public Properties getValue() {
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
