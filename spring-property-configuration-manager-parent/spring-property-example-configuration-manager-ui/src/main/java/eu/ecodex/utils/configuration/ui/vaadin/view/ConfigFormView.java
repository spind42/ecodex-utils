package eu.ecodex.utils.configuration.ui.vaadin.view;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.HtmlImport;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.binder.BinderValidationStatus;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import eu.ecodex.utils.configuration.example1.Example1ConfigurationProperties;
import eu.ecodex.utils.configuration.ui.vaadin.tools.ConfigurationFormFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;



@HtmlImport("styles/shared-styles.html")
@Route("configform")
@PageTitle("Spring Properties Configuration Manager")
public class ConfigFormView extends VerticalLayout {

    private static final Logger LOGGER = LogManager.getLogger(ConfigFormView.class);

    @Autowired
    ConfigurationFormFactory configurationFormFactory;

//    @Autowired
    Button button = new Button("Check");

    ConfigurationFormFactory.ConfigurationPropertyForm formFromConfigurationPropertiesClass;

    public ConfigFormView() {

    }

    @PostConstruct
    public void init() {
        formFromConfigurationPropertiesClass = configurationFormFactory.createFormFromConfigurationPropertiesClass(Example1ConfigurationProperties.class);
        this.add(formFromConfigurationPropertiesClass);
        this.add(button);

        button.addClickListener(this::checkButtonClicked);
    }

    private void checkButtonClicked(ClickEvent<Button> buttonClickEvent) {
        LOGGER.info("check button clicked");
//        formFromConfigurationPropertiesClass.getValue();
        BinderValidationStatus validate = formFromConfigurationPropertiesClass.getBinder().validate();
        LOGGER.info("validate result: [{}]", validate.getBeanValidationResults());
    }

}
