package eu.ecodex.utils.configuration.ui.vaadin.view;

import com.vaadin.flow.component.dependency.HtmlImport;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import eu.ecodex.utils.configuration.domain.ConfigurationProperty;
import eu.ecodex.utils.configuration.service.ConfigurationPropertyManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.List;

@HtmlImport("styles/shared-styles.html")
@Route("")
@PageTitle("Spring Properties Configuration Manager")
public class MainView extends VerticalLayout {

    private static final Logger LOGGER = LogManager.getLogger(MainView.class);

    Grid<ConfigurationProperty> grid = new Grid<>(ConfigurationProperty.class);

    @Autowired
    ConfigurationPropertyManager configurationPropertyManager;

    public MainView() {
        this.add(this.grid);

    }

    @PostConstruct
    public void init() {
        List<ConfigurationProperty> all = configurationPropertyManager.getConfigurationProperties("eu.ecodex.utils.configuration.example1");

        LOGGER.debug("all is {}", all);

        grid.setItems(all);
//        grid.setColumns("propertyName", "description", "label");

    }




}
