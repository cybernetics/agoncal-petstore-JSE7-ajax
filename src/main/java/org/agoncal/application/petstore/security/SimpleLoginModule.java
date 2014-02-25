package org.agoncal.application.petstore.security;

import java.util.*;

import javax.ejb.EJBException;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.*;
import javax.naming.*;
import javax.persistence.NoResultException;
import javax.security.auth.Subject;
import javax.security.auth.callback.*;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;
import javax.validation.*;

import org.agoncal.application.petstore.domain.Customer;
import org.agoncal.application.petstore.service.CustomerService;

/**
 * @author blep Date: 12/02/12 Time: 11:59
 */

public class SimpleLoginModule implements LoginModule {

    // ======================================
    // = Attributes =
    // ======================================

    private BeanManager beanManager;

    private CallbackHandler callbackHandler;

    private CustomerService customerService;

    // ======================================
    // = Business methods =
    // ======================================

    @Override
    public boolean abort() throws LoginException {
        return true;
    }

    @Override
    public boolean commit() throws LoginException {
        return true;
    }

    private CustomerService getCustomerService() {
        if (customerService != null) {
            return customerService;
        }
        try {
            Context context = new InitialContext();
            beanManager = (BeanManager) context.lookup("java:comp/BeanManager");
            Bean<?> bean = beanManager.getBeans(CustomerService.class).iterator().next();
            CreationalContext cc = beanManager.createCreationalContext(bean);
            customerService = (CustomerService) beanManager.getReference(bean, CustomerService.class, cc);

        }
        catch (NamingException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        return customerService;

    }

    @Override
    public void initialize(Subject subject, CallbackHandler callbackHandler, Map<String, ?> stringMap,
            Map<String, ?> stringMap1) {
        this.callbackHandler = callbackHandler;
        getCustomerService();
    }

    @Override
    public boolean login() throws LoginException {

        NameCallback nameCallback = new NameCallback("Name : ");
        PasswordCallback passwordCallback = new PasswordCallback("Password : ", false);
        try {
            callbackHandler.handle(new Callback[] { nameCallback, passwordCallback });
            String username = nameCallback.getName();
            String password = new String(passwordCallback.getPassword());
            nameCallback.setName("");
            passwordCallback.clearPassword();
            Customer customer = customerService.findCustomer(username, password);

            if (customer == null) {
                throw new LoginException("Authentication failed");
            }

            return true;
        }
        catch (EJBException mightBeError) {
            if ((mightBeError.getCause() != null) && (mightBeError.getCause() instanceof NoResultException)) {
                throw new LoginException("Authentification failed. Did you enter user name and password correctly?");
            }
            else if ((mightBeError.getCause() != null)
                    && (mightBeError.getCause() instanceof ConstraintViolationException)) {
                String messages = "";
                ConstraintViolationException cve = (ConstraintViolationException) mightBeError.getCause();
                Set<ConstraintViolation<?>> violations = cve.getConstraintViolations();
                for (ConstraintViolation<?> v : violations) {
                    messages += v.getMessage() + ". ";
                }
                throw new LoginException(messages);
            }
            else {
                throw new LoginException("Database problem" + mightBeError.getMessage());
            }
        }
        catch (Exception e) {
            if (e instanceof LoginException) {
                throw (LoginException) e;
            }
            e.printStackTrace();
            throw new LoginException(e.getMessage());
        }
    }

    @Override
    public boolean logout() throws LoginException {
        return true;
    }
}
