package eu.domibus.connector.web.login;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyPressEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.login.LoginOverlay;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.*;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import eu.domibus.connector.service.IUserPasswordService;
import eu.domibus.connector.web.auth.exception.InitialPasswordException;
import eu.domibus.connector.web.auth.exception.UserLoginException;
import eu.domibus.connector.web.layout.DashboardView;
import eu.domibus.connector.web.layout.DomibusConnectorAdminHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.AuthenticationException;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;

@SpringComponent
@UIScope
@Route(value = LoginView.ROUTE)
@PageTitle("domibusConnector - Login")
public class LoginView extends VerticalLayout implements HasUrlParameter<String>, BeforeEnterObserver {

	public static final String ROUTE = "login";
	public static final String PREVIOUS_ROUTE_PARAMETER = "afterLoginGoTo";

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private DomibusConnectorAdminHeader header;

	@Autowired
	IUserPasswordService userPasswordService;

	private LoginOverlay login = new LoginOverlay();

	private String afterLoginGoTo = DashboardView.ROUTE;

	private TextField username = new TextField();
	private PasswordField password = new PasswordField();

	@Override
	public void setParameter(BeforeEvent event, @OptionalParameter String parameter) {
//		QueryParameters queryParameters = event.getLocation().getQueryParameters();
//		List<String> strings = queryParameters.getParameters().get(PREVIOUS_ROUTE_PARAMETER);
//		if (!CollectionUtils.isEmpty(strings)) {
//			afterLoginGoTo = strings.get(0);
//		}
		if (!StringUtils.isEmpty(parameter) && !LoginView.ROUTE.equals(parameter)) {
//			afterLoginGoTo = parameter;
		}

	}

	public LoginView() {}

	@PostConstruct
	public void init() {
		this.authenticationManager = authenticationManager;
//		this.webUserService = userService;

		login.setAction("login"); // 
        getElement().appendChild(login.getElement()); 


		add(new DomibusConnectorAdminHeader());
		
		HorizontalLayout login = new HorizontalLayout();
		VerticalLayout loginArea = new VerticalLayout();
		
		Button loginButton = new Button("Login");
		
		Div usernameDiv = new Div();

		username.setLabel("Username");
		username.setAutofocus(true);
		username.addKeyPressListener(Key.ENTER, new ComponentEventListener<KeyPressEvent>() {
			
			@Override
			public void onComponentEvent(KeyPressEvent event) {
				loginButton.click();
				
			}
		});
		usernameDiv.add(username);
		usernameDiv.getStyle().set("text-align", "center");
		loginArea.add(usernameDiv);
		
		
		Div passwordDiv = new Div();

		password.setLabel("Password");
		password.addKeyPressListener(Key.ENTER, new ComponentEventListener<KeyPressEvent>() {
			
			@Override
			public void onComponentEvent(KeyPressEvent event) {
				loginButton.click();
				
			}
		});
		passwordDiv.add(password);
		passwordDiv.getStyle().set("text-align", "center");
		loginArea.add(passwordDiv);
		
		
		Div loginButtonContent = new Div();
		loginButtonContent.getStyle().set("text-align", "center");
		loginButtonContent.getStyle().set("padding", "10px");
		
		loginButton.addClickListener(this::loginButtonClicked);
		loginButtonContent.add(loginButton);
		
		Button changePasswordButton = new Button("Change Password");
		changePasswordButton.addClickListener(e -> {
			if(username.getValue().isEmpty()) {
				Dialog errorDialog = new LoginErrorDialog("The field \"Username\" must not be empty!");
				username.clear();
				password.clear();
				errorDialog.open();
				return;
			}
			Dialog changePasswordDialog = new ChangePasswordDialog(userPasswordService,username.getValue(), password.getValue());
			username.clear();
			password.clear();
//			close();
			changePasswordDialog.open();
		});
		loginButtonContent.add(changePasswordButton);
		
		
		loginArea.add(loginButtonContent);
		
		loginArea.setSizeFull();
		loginArea.setAlignItems(Alignment.CENTER);
		loginArea.getStyle().set("align-items", "center");
		login.add(loginArea);
		login.setVerticalComponentAlignment(Alignment.CENTER, loginArea);
		
		add(loginArea);
		
//		Dialog loginDialog = new LoginDialog(userService);
//		
//		loginDialog.open();
	}

	private void loginButtonClicked(ClickEvent<Button> buttonClickEvent) {
		if(username.getValue().isEmpty()) {
			Dialog errorDialog = new LoginErrorDialog("The field \"Username\" must not be empty!");
			username.clear();
			password.clear();
			errorDialog.open();
			return;
		}
		if(password.getValue().isEmpty()) {
			Dialog errorDialog = new LoginErrorDialog("The field \"Password\" must not be empty!");
			password.clear();
			errorDialog.open();
			return;
		}
		try {
			userPasswordService.passwordLogin(username.getValue(), password.getValue());
		} catch (UserLoginException e1) {
			Dialog errorDialog = new LoginErrorDialog(e1.getMessage());
			username.clear();
			password.clear();
			errorDialog.open();
			return;
		} catch (InitialPasswordException e1) {
			Dialog changePasswordDialog = new ChangePasswordDialog(userPasswordService,username.getValue(), password.getValue());
			username.clear();
			password.clear();
//				close();
			changePasswordDialog.open();
		} catch (AuthenticationException authException) {
			//show error message...
		}
		//TODO: navigate to previous route...
//			getUI().ifPresent(ui -> ui);
		this.getUI().ifPresent(ui -> ui.navigate(afterLoginGoTo));
//			close();
	}

	@Override
	public void beforeEnter(BeforeEnterEvent event) {
		//TODO: check if current IUserPasswordService supports username/password login
		//if not redirect to login service...
//		IUserPasswordService
	}
}
