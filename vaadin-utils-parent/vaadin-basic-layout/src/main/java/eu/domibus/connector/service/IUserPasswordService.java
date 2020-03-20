package eu.domibus.connector.service;


import eu.domibus.connector.web.auth.exception.UserLoginException;
import org.springframework.security.core.AuthenticationException;

/**
 * Interface for the username password login service
 *  maybe rename this interface to a more generic name
 *  also add support to redirect to central login service
 *
 */
public interface IUserPasswordService {

    void changePasswordLogin(String username, String value, String value1) throws AuthenticationException;

    void passwordLogin(String username, String password) throws AuthenticationException;

    //TODO: add method with supported features, listUsers, changePassword, maybe with an reroute option

}
