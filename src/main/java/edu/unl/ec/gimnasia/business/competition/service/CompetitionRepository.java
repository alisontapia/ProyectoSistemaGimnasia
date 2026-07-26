package edu.unl.ec.gimnasia.business.competition.service;

import edu.unl.ec.gimnasia.business.service.CrudGenericService;
import edu.unl.ec.gimnasia.domain.competition.Competition;
import edu.unl.ec.gimnasia.exception.EntityNotFoundException;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;

import java.io.Serializable;
import java.util.List;

@Stateless
public class CompetitionRepository implements Serializable {

    @Inject
    private CrudGenericService crudService;

    public Competition save(Competition competition) {
        if (competition.getId() == null) {
            return crudService.create(competition);
        }
        return crudService.update(competition);
    }

    public Competition find(Long id) throws EntityNotFoundException {
        Competition competition = crudService.find(Competition.class, id);
        if (competition == null) {
            throw new EntityNotFoundException(" Competencia no encontrada con id [" + id + "] ");
        }
        return competition;
    }

    public List<Competition> findAll() {
        return crudService.findWithNamedQuery("Competition.findAll");
    }
}
