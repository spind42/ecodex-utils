package eu.domibus.connector.service.impl;

import eu.domibus.connector.service.IUserPasswordService;
import eu.domibus.connector.web.auth.exception.NotSupportedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@ConditionalOnMissingBean
@Component
public class DefaultUserPasswordService implements IUserPasswordService {

    @Autowired
    AuthenticationManager authenticationManager;

    public void changePasswordLogin(String username, String oldPassword, String newPassword) throws NotSupportedException {
        throw new NotSupportedException("", "The current authentication implementation does not support password change!");
    }

    @Override
    public void passwordLogin(String username, String password) throws AuthenticationException {
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(username, password);
        Authentication authentication = authenticationManager.authenticate(usernamePasswordAuthenticationToken);
        if (authentication.isAuthenticated()) {
            SecurityContext context = SecurityContextHolder.getContext();
            context.setAuthentication(authentication);
        }
    }

}
