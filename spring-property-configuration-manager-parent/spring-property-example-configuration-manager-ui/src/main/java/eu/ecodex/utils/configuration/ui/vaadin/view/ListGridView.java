package eu.ecodex.utils.configuration.ui.vaadin.view;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.dependency.HtmlImport;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.function.ValueProvider;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import eu.ecodex.utils.configuration.domain.ConfigurationProperty;
import eu.ecodex.utils.configuration.service.ConfigurationPropertyCollector;
import eu.ecodex.utils.configuration.ui.vaadin.tools.ConfigurationFormFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

@HtmlImport("styles/shared-styles.html")
@Route(value = "listgridview", layout = MainView.class)
@PageTitle("Spring Properties Configuration Manager")
public class ListGridView extends VerticalLayout {

    private static final Logger LOGGER = LogManager.getLogger(ListGridView.class);

    Grid<ConfigurationProperty> grid = new Grid<>(ConfigurationProperty.class, false);

    @Autowired
    ConfigurationPropertyCollector configurationPropertyCollector;

    @Autowired
    ConfigurationFormFactory configurationFormFactory;

    Properties properties = new Properties();

    Binder<Properties> binder = new Binder();

    public ListGridView() {


    }

    @PostConstruct
    public void init() {
        Collection<ConfigurationProperty> all = configurationPropertyCollector.getConfigurationProperties("eu.ecodex.utils.configuration.example1");

        LOGGER.debug("all is {}", all);

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



        grid.setItems(all);
        this.setSizeFull();
        this.add(this.grid);

    }




}
