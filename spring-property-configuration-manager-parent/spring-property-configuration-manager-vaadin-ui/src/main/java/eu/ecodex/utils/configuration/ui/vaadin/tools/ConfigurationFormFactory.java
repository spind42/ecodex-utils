package eu.ecodex.utils.configuration.ui.vaadin.tools;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.spring.annotation.UIScope;
import eu.ecodex.utils.configuration.domain.ConfigurationProperty;
import eu.ecodex.utils.configuration.service.ConfigurationPropertyCollector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;

import java.lang.annotation.Annotation;
import java.util.List;

@Service
@UIScope
public class ConfigurationFormFactory {

    @Autowired
    ConfigurationPropertyCollector configurationPropertyCollector;

    public Component createFormFromConfigurationPropertiesClass(Class clazz) {
        if (!clazz.isAnnotationPresent(ConfigurationProperties.class)) {
            throw new IllegalArgumentException("the passed class must be annotated with " + ConfigurationProperties.class);
        }
        List<ConfigurationProperty> configurationPropertyFromClazz = configurationPropertyCollector.getConfigurationPropertyFromClazz(clazz);



        FormLayout formLayout = new FormLayout();

        configurationPropertyFromClazz.forEach(prop -> {
            String label = prop.getLabel();
            Component c = createComponentFromConfigurationProperty(prop);

            formLayout.addFormItem(c, label);
        });
        return formLayout;
    }

    public Component createComponentFromConfigurationProperty(ConfigurationProperty prop) {

        //TODO: binder code...
        return new TextField();
    }



}
