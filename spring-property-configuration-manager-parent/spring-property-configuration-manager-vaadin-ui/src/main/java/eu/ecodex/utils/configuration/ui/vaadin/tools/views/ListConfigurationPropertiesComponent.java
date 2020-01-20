package eu.ecodex.utils.configuration.ui.vaadin.tools.views;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.binder.Validator;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.function.ValueProvider;
import eu.ecodex.utils.configuration.domain.ConfigurationProperty;
import eu.ecodex.utils.configuration.service.ConfigurationPropertyCollector;
import eu.ecodex.utils.configuration.ui.vaadin.tools.ConfigurationFormFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@org.springframework.stereotype.Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class ListConfigurationPropertiesComponent extends VerticalLayout {

    private static final Logger LOGGER = LogManager.getLogger(ListConfigurationPropertiesComponent.class);

    Grid<ConfigurationProperty> grid = new Grid<>(ConfigurationProperty.class, false);

    @Autowired
    ConfigurationPropertyCollector configurationPropertyCollector;

    @Autowired
    ConfigurationFormFactory configurationFormFactory;

    Properties properties = new Properties();

    Binder<Properties> binder = new Binder();

    private Collection<ConfigurationProperty> configurationProperties = new ArrayList<>();

    public ListConfigurationPropertiesComponent() {
    }

    @PostConstruct
    public void init() {

        binder.setBean(properties);

        grid.addColumn("propertyName").setHeader("Property Path");
        grid.addColumn("label").setHeader("Label");
        grid.addColumn("type").setHeader("Type");
        grid.addComponentColumn(new ValueProvider<ConfigurationProperty, Component>() {
            @Override
            public Component apply(ConfigurationProperty configurationProperty) {
                AbstractField field = configurationFormFactory.createField(configurationProperty, binder);

                return field;
            }
        });
        grid.setDetailsVisibleOnClick(true);
        grid.setItemDetailsRenderer(new ComponentRenderer<>(configProp -> {
            VerticalLayout vl = new VerticalLayout();
            vl.add(new Label("Description"));
            vl.add(new Label(configProp.getDescription()));
            return vl;
        }));


        grid.setItems(configurationProperties);

        //TODO: add validation error field before ListView

        this.setSizeFull();
        this.add(this.grid);

    }

    public Binder<Properties> getBinder() {
        return binder;
    }

    public void setBinder(Binder<Properties> binder) {
        this.binder = binder;
    }

    public Collection<ConfigurationProperty> getConfigurationProperties() {
        return configurationProperties;
    }

    public void setConfigurationProperties(Collection<ConfigurationProperty> configurationProperties) {
        this.configurationProperties = configurationProperties;
        this.grid.setItems(configurationProperties);


        List<Class> configClasses = configurationProperties
                .stream()
                .map(prop -> prop.getParentClass())
                .distinct()
                .collect(Collectors.toList());

        binder.withValidator(new Validator<Properties>() {
            @Override
            public ValidationResult apply(Properties value, ValueContext context) {

                return null;
            }
        });
    }

    public void validate() {
        this.binder.validate();
    }
}
