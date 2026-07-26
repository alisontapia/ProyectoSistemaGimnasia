package edu.unl.ec.gimnasia.view;

import edu.unl.ec.gimnasia.faces.FacesUtil;
import edu.unl.ec.gimnasia.view.security.UserSessionBean;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.IOException;
import java.io.Serializable;

@Named
@RequestScoped
public class MenuBarController implements Serializable {

    @Inject
    private UserSessionBean userSessionBean;

    public void logout() throws IOException {

        userSessionBean.logout();

        FacesUtil.addSuccessMessageAndKeep("Aviso", "Regresa pronto");

        FacesContext context = FacesContext.getCurrentInstance();

        context.getExternalContext().redirect(
                context.getExternalContext().getRequestContextPath() + "/index.xhtml");
    }
}
