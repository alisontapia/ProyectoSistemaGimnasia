package edu.unl.ec.gimnasia.domain.security;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Representa un rol dentro del sistema de seguridad.
 *
 * Un rol agrupa un conjunto de permisos que determinan
 * las acciones que un usuario puede realizar sobre los
 * recursos de la aplicación.
 *
 * Ejemplos de roles definidos:
 * - ADMINISTRADOR
 * - JUEZ
 *
 * Los roles son asignados a los usuarios para gestionar
 * la autorización y el control de acceso del sistema.
 */

@Entity
@Table(name = "role")
@NamedQueries({
        @NamedQuery(name = "Role.findByName", query = "SELECT r FROM Role r WHERE r.name = :name"),
        @NamedQuery(name = "Role.findAll", query = "SELECT r FROM Role r ORDER BY r.name")
})
public class Role implements Serializable {

    public static final String ADMINISTRADOR = "ADMINISTRADOR";
    public static final String JUEZ = "JUEZ";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull @NotEmpty
    @Column(nullable = false, unique = true, length = 40)
    private String name;

    @Column(length = 200)
    private String description;

    @ManyToMany
    @JoinTable(name = "role_permission",
            joinColumns = @JoinColumn(name = "role_id"),
            inverseJoinColumns = @JoinColumn(name = "permission_id"))
    private Set<Permission> permissions;

    public Role() {
        this.permissions = new HashSet<>();
    }

    public Role(Long id, @NotNull @NotEmpty String name, String description) {
        this();
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public void add(Permission permission) {
        if (permission != null && !permissions.contains(permission)) {
            permissions.add(permission);
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name.trim();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<Permission> getPermissions() {
        return permissions;
    }

    public void setPermissions(Set<Permission> permissions) {
        this.permissions = permissions;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Role role = (Role) o;
        return Objects.equals(id, role.id) && Objects.equals(name, role.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}
