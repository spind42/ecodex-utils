package eu.domibus.connector.web.layout;

import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.UIScope;


@UIScope
@Route(value = DashboardView.ROUTE, layout = BasicLayout.class)
@PageTitle("domibusConnector - Administrator")
public class DashboardView extends VerticalLayout {

	public static final String ROUTE = "";

	Label l = new Label();
	
    public DashboardView() {
    	l.setText("Welcome to Domibus Connector Administration UI");
		add(l);
    }


}
