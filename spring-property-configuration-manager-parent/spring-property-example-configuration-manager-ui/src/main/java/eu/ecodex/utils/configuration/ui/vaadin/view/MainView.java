package eu.ecodex.utils.configuration.ui.vaadin.view;

import com.vaadin.flow.component.dependency.HtmlImport;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import eu.ecodex.utils.configuration.example1.Example1Package;
import eu.ecodex.utils.configuration.service.ConfigurationPropertyManager;
import org.atmosphere.config.service.Post;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

@HtmlImport("styles/shared-styles.html")
@Route("")
@PageTitle("Spring Properties Configuration Manager")
public class MainView {



    @Autowired
    ConfigurationPropertyManager configurationPropertyManager;


    @PostConstruct
    public void init() {
        configurationPropertyManager.getAll(Example1Package.class);
    }

}
