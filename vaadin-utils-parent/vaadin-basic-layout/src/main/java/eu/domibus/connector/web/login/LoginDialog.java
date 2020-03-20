package eu.domibus.connector.web.login;

import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyPressEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import eu.domibus.connector.service.IUserPasswordService;
import eu.domibus.connector.web.auth.exception.InitialPasswordException;
import eu.domibus.connector.web.auth.exception.UserLoginException;
import eu.domibus.connector.web.layout.DashboardView;

//TODO: add configureable option for default redirect to VIEW
public class LoginDialog extends Dialog {
	
	Button loginButton = new Button("Login");

	public LoginDialog(IUserPasswordService userService) {
		Div usernameDiv = new Div();
		TextField username = new TextField();
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
		add(usernameDiv);
		
		
		Div passwordDiv = new Div();
		PasswordField password = new PasswordField();
		password.setLabel("Password");
		password.addKeyPressListener(Key.ENTER, new ComponentEventListener<KeyPressEvent>() {
			
			@Override
			public void onComponentEvent(KeyPressEvent event) {
				loginButton.click();
				
			}
		});
		passwordDiv.add(password);
		passwordDiv.getStyle().set("text-align", "center");
		add(passwordDiv);
		
		
		Div loginButtonContent = new Div();
		loginButtonContent.getStyle().set("text-align", "center");
		loginButtonContent.getStyle().set("padding", "10px");
		
		loginButton.addClickListener(e -> {
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
				userService.passwordLogin(username.getValue(), password.getValue());
			} catch (UserLoginException e1) {
				Dialog errorDialog = new LoginErrorDialog(e1.getMessage());
				username.clear();
				password.clear();
				errorDialog.open();
				return;
			} catch (InitialPasswordException e1) {
				Dialog changePasswordDialog = new ChangePasswordDialog(userService,username.getValue(), password.getValue());
				username.clear();
				password.clear();
				close();
				changePasswordDialog.open();
			}
			this.getUI().ifPresent(ui -> ui.navigate(DashboardView.class));
			close();
		});
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
			Dialog changePasswordDialog = new ChangePasswordDialog(userService,username.getValue(), password.getValue());
			username.clear();
			password.clear();
			close();
			changePasswordDialog.open();
		});
		loginButtonContent.add(changePasswordButton);
		
		
		add(loginButtonContent);
	}

}
