package edu.unl.ec.gimnasia.view.security;

import edu.unl.ec.gimnasia.domain.security.User;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;

import java.io.Serializable;

@Named
@SessionScoped
public class UserSessionBean implements Serializable {

    private UserPrincipalDTO principal;

    public void login(User user) {
        this.principal = new UserPrincipalDTO(user);
    }

    public void logout() {
        this.principal = null;
    }

    public boolean isAuthenticated() {
        return principal != null;
    }

    public boolean hasRole(String roleName) {
        return principal != null && principal.hasRole(roleName);
    }

    public UserPrincipalDTO getUser() {
        return principal;
    }
}
