package edu.unl.ec.gimnasia.business.administrator.service;

import edu.unl.ec.gimnasia.business.service.CrudGenericService;
import edu.unl.ec.gimnasia.domain.people.Administrator;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;

import java.io.Serializable;

@Stateless
public class AdministratorRepository implements Serializable {

    private static final Long SYSTEM_ADMIN_ID = 1L;

    @Inject
    private CrudGenericService crudService;

    public Administrator getSystemAdmin() {
        Administrator administrator = crudService.find(Administrator.class, SYSTEM_ADMIN_ID);
        if (administrator == null) {
            throw new IllegalStateException(
                    "No se encontro el administrador del sistema (id=" + SYSTEM_ADMIN_ID + "). "
                            + "Verifique que db/initial-data.sql se haya ejecutado.");
        }
        return administrator;
    }
}
