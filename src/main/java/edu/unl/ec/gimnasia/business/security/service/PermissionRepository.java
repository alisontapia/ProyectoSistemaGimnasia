package edu.unl.ec.gimnasia.business.security.service;

import edu.unl.ec.gimnasia.business.service.CrudGenericService;
import edu.unl.ec.gimnasia.domain.security.Permission;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;

import java.io.Serializable;
import java.util.List;

@Stateless
public class PermissionRepository implements Serializable {

    @Inject
    private CrudGenericService crudService;

    public List<Permission> findAll() {
        return crudService.findWithNamedQuery("Permission.findAll");
    }

    public Permission find(Long id) {
        return crudService.find(Permission.class, id);
    }
}
