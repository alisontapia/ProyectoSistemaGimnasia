package edu.unl.ec.gimnasia.domain.security;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.util.Objects;

/**
 * Representa un permiso de acceso dentro del sistema.
 *
 * Un permiso define qué acción puede realizarse sobre
 * un recurso específico, como una página o funcionalidad.
 *
 * Los permisos son asignados a roles y utilizados por el
 * módulo de seguridad para controlar el acceso de los usuarios
 * a las distintas opciones del sistema.
 */

@Entity
@Table(name = "permission")
@NamedQuery(name = "Permission.findAll", query = "SELECT p FROM Permission p ORDER BY p.id")
public class Permission implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull @NotEmpty
    @Column(nullable = false, length = 100)
    private String resource;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ActionType action;

    public Permission() {
        this.action = ActionType.ALL;
    }

    public Permission(Long id, @NotNull @NotEmpty String resource, @NotNull ActionType action) {
        this.id = id;
        this.resource = resource;
        this.action = action;
    }

    public Permission(@NotNull @NotEmpty String resource, @NotNull ActionType action) {
        this(null, resource, action);
    }

    public boolean matchWith(String requestResource, ActionType requestAction) {
        return this.resource.equals(requestResource)
                && (this.action == ActionType.ALL || this.action.equals(requestAction));
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public ActionType getAction() {
        return action;
    }

    public void setAction(ActionType action) {
        this.action = action;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Permission that = (Permission) o;
        return Objects.equals(id, that.id) && Objects.equals(resource, that.resource) && action == that.action;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, resource, action);
    }
}
