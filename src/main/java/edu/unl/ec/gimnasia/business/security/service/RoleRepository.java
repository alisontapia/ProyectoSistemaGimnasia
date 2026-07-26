package edu.unl.ec.gimnasia.business.security.service;

import edu.unl.ec.gimnasia.business.service.CrudGenericService;
import edu.unl.ec.gimnasia.domain.security.Role;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.Serializable;

@Stateless
public class RoleRepository implements Serializable {

    @Inject
    private CrudGenericService crudService;

    public Role find(String name) {
        Map<String, Object> params = new HashMap<>();
        params.put("name", name);
        return crudService.findSingleResultOrNullWithNamedQuery("Role.findByName", params);
    }

    public List<Role> findAll() {
        return crudService.findWithNamedQuery("Role.findAll");
    }
}
