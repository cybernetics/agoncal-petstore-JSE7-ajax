package org.agoncal.application.petstore.web;

import java.io.Serializable;
import java.util.logging.Logger;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.interceptor.*;
import javax.security.auth.login.LoginException;

/**
 * @author Antonio Goncalves http://www.antoniogoncalves.org -- This interceptor catches exception and displayes them in
 *         a JSF page
 */
@Interceptor
@CatchException
public class ExceptionInterceptor implements Serializable {

    @Inject
    private Logger log;

    // TODO to refactor with Controller methods
    protected void addErrorMessage(String message) {
        FacesContext context = FacesContext.getCurrentInstance();
        context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, message, null));
    }

    @AroundInvoke
    public Object catchException(InvocationContext ic) throws Exception {
        try {
            return ic.proceed();
        }
        catch (LoginException nonTechnicalError) {
            addErrorMessage(nonTechnicalError.getMessage());
        }
        catch (Exception e) {
            addErrorMessage(e.getMessage());
            log.severe("/!\\ " + ic.getTarget().getClass().getName() + " - " + ic.getMethod().getName() + " - "
                    + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
}
