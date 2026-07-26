package edu.unl.ec.gimnasia.business.evaluation.service;

import edu.unl.ec.gimnasia.business.service.CrudGenericService;
import edu.unl.ec.gimnasia.domain.evaluation.Result;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Stateless
public class ResultRepository implements Serializable {

    @Inject
    private CrudGenericService crudService;

    public Result save(Result result) {
        if (result.getId() == null) {
            return crudService.create(result);
        }
        return crudService.update(result);
    }

    public Result findByGymnastAndCompetition(Long gymnastId, Long competitionId) {
        Map<String, Object> params = new HashMap<>();
        params.put("gymnastId", gymnastId);
        params.put("competitionId", competitionId);
        return crudService.findSingleResultOrNullWithNamedQuery("Result.findByGymnastAndCompetition", params);
    }

    public List<Result> findAllByCompetition(Long competitionId) {
        Map<String, Object> params = new HashMap<>();
        params.put("competitionId", competitionId);
        return crudService.findWithNamedQuery("Result.findAllByCompetition", params);
    }

    public List<Result> findAllByGymnast(Long gymnastId) {
        Map<String, Object> params = new HashMap<>();
        params.put("gymnastId", gymnastId);
        return crudService.findWithNamedQuery("Result.findAllByGymnast", params);
    }
}
