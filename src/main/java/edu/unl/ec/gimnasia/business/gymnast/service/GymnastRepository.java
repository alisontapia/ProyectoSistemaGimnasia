package edu.unl.ec.gimnasia.business.gymnast.service;

import edu.unl.ec.gimnasia.business.service.CrudGenericService;
import edu.unl.ec.gimnasia.domain.people.Gymnast;
import edu.unl.ec.gimnasia.exception.AlreadyEntityException;
import edu.unl.ec.gimnasia.exception.EntityNotFoundException;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Stateless
public class GymnastRepository implements Serializable {

    @Inject
    private CrudGenericService crudService;

    public Gymnast save(Gymnast gymnast) throws AlreadyEntityException {
        if (gymnast.getId() == null) {
            if (existsByNationalId(gymnast.getNationalId())) {
                throw new AlreadyEntityException(
                        "Ya existe una gimnasta registrada con cedula [" + gymnast.getNationalId() + "]");
            }
            return crudService.create(gymnast);
        }
        return crudService.update(gymnast);
    }

    public Gymnast find(Long id) throws EntityNotFoundException {
        Gymnast gymnast = crudService.find(Gymnast.class, id);
        if (gymnast == null) {
            throw new EntityNotFoundException("Gimnasta no encontrada con id [" + id + "]");
        }
        return gymnast;
    }

    public List<Gymnast> findAll() {
        return crudService.findWithNamedQuery("Gymnast.findAll");
    }

    private boolean existsByNationalId(String nationalId) {
        Map<String, Object> params = new HashMap<>();
        params.put("nationalId", nationalId);
        List<Gymnast> found = crudService.findWithNamedQuery("Gymnast.findByNationalId", params);
        return !found.isEmpty();
    }
}
