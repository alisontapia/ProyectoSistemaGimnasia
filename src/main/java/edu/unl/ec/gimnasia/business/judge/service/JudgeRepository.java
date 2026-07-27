package edu.unl.ec.gimnasia.business.judge.service;

import edu.unl.ec.gimnasia.business.service.CrudGenericService;
import edu.unl.ec.gimnasia.domain.people.Judge;
import edu.unl.ec.gimnasia.exception.EntityNotFoundException;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Stateless
public class JudgeRepository implements Serializable {

    @Inject
    private CrudGenericService crudService;

    public Judge save(Judge judge) {
        if (judge.hasAccount() && judge.getUser().getId() == null) {
            crudService.create(judge.getUser());
        }
        if (judge.getId() == null) {
            return crudService.create(judge);
        }
        return crudService.update(judge);
    }

    public Judge find(Long id) throws EntityNotFoundException {
        Judge judge = crudService.find(Judge.class, id);
        if (judge == null) {
            throw new EntityNotFoundException("Juez no encontrado con id [" + id + "]");
        }
        return judge;
    }

    public Judge findByUsername(String username) throws EntityNotFoundException {
        Map<String, Object> params = new HashMap<>();
        params.put("username", username);
        Judge judge = crudService.findSingleResultOrNullWithNamedQuery("Judge.findByUsername", params);
        if (judge == null) {
            throw new EntityNotFoundException("Juez no encontrado para el usuario [" + username + "]");
        }
        return judge;
    }

    /**
     * Recupera todos los jueces registrados en el sistema.
     */
    public List<Judge> findAll() {
        return crudService.findWithNamedQuery("Judge.findAll");
    }
    public List<Judge> findWithoutAccount() {
        return crudService.findWithNamedQuery("Judge.findWithoutAccount");
    }

    public void delete(Long id) throws EntityNotFoundException {
        find(id);
        crudService.delete(Judge.class, id);
    }
}
