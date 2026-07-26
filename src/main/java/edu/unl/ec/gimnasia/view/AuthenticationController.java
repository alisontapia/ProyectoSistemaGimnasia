package edu.unl.ec.gimnasia.view;

import edu.unl.ec.gimnasia.business.security.SecurityFacade;
import edu.unl.ec.gimnasia.domain.security.User;
import edu.unl.ec.gimnasia.exception.CredentialInvalidException;
import edu.unl.ec.gimnasia.faces.FacesUtil;
import edu.unl.ec.gimnasia.view.security.UserSessionBean;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.io.Serializable;

@Named
@ViewScoped
public class AuthenticationController implements Serializable {

    @NotNull @NotEmpty @Size(min = 4, message = "Nombre de usuario muy corto")
    private String username;

    @NotNull @NotEmpty @Size(min = 4, message = "Contraseña muy corta")
    private String password;

    @Inject
    private SecurityFacade securityFacade;

    @Inject
    private UserSessionBean userSessionBean;

    public String login() {
        try {
            User user = securityFacade.authenticate(username, password);
            userSessionBean.login(user);
            FacesUtil.addSuccessMessageAndKeep("Bienvenido/a", user.getUsername());
            return "/dashboard.xhtml?faces-redirect=true";
        } catch (CredentialInvalidException e) {
            FacesUtil.addErrorMessage("Ingreso inválido", e.getMessage());
            return null;
        }
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
