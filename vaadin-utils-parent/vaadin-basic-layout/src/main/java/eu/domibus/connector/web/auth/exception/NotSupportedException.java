package eu.domibus.connector.web.auth.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * This exception will be thrown
 * if the requested user action is not supported
 *  eg. password change not supported
 */
public class NotSupportedException extends AuthenticationException {

    /**
     * A url to a service which is able to do the requested
     * action. eg Authentication Portal
     */
    String redirectUrl;

    public NotSupportedException(String redirectUrl, String msg, Throwable t) {
        super(msg, t);
        this.redirectUrl = redirectUrl;
    }

    public NotSupportedException(String redirectUrl, String msg) {
        super(msg);
        this.redirectUrl = redirectUrl;
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }

    public void setRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }
}
