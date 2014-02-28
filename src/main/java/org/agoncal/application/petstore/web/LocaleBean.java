package org.agoncal.application.petstore.web;

import java.io.Serializable;
import java.util.Locale;
import java.util.logging.Logger;

import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Produces;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.inject.*;

import org.agoncal.application.petstore.util.Loggable;

@Named
@SessionScoped
@Loggable
public class LocaleBean implements Serializable {
    private static final long serialVersionUID = 7260637678996003208L;

    @Produces
    private Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();

    @Inject
    private transient Logger logger;

    public String getLanguage() {
        return locale.getLanguage();
    }

    public Locale getLocale() {
        return locale;
    }

    public void languageSelectionChanged(final AjaxBehaviorEvent event) {
        logger.info("Language combobox has been changed");
    }

    public void setLanguage(String language) {
        if (!locale.getLanguage().equals(language)) {
            logger.info("New language: " + language);
        }
        locale = new Locale(language);
        FacesContext.getCurrentInstance().getViewRoot().setLocale(locale);
    }
}