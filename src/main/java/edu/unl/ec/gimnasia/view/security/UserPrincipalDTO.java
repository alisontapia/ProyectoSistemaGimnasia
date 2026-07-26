package edu.unl.ec.gimnasia.view.security;

import edu.unl.ec.gimnasia.domain.security.ActionType;
import edu.unl.ec.gimnasia.domain.security.User;

import java.io.Serializable;
import java.security.Principal;

public class UserPrincipalDTO implements Principal, Serializable {

    private final User user;

    public UserPrincipalDTO(User user) {
        this.user = user;
    }

    public boolean hasPermissionForPage(String resource) {
        return hasPermission(resource, ActionType.READ);
    }

    public boolean hasPermission(String resource, ActionType action) {
        return user.getRoles().stream()
                .flatMap(role -> role.getPermissions().stream())
                .anyMatch(permission -> permission.matchWith(resource, action));
    }

    public boolean hasRole(String roleName) {
        return user.hasRole(roleName);
    }

    public User getUser() {
        return user;
    }

    @Override
    public String getName() {
        return user.getUsername();
    }
}
