package eu.domibus.connector.web.layout;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.spring.annotation.UIScope;
import eu.domibus.connector.web.utils.TabViewRouterHelper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@UIScope
@org.springframework.stereotype.Component
public class BasicLayout extends AppLayout implements BeforeEnterObserver {

    protected TabViewRouterHelper tabViewRouterHelper = new TabViewRouterHelper();

    public BasicLayout() {}

    public void beforeEnter(BeforeEnterEvent event) {
        tabViewRouterHelper.beforeEnter(event);
    }

}
