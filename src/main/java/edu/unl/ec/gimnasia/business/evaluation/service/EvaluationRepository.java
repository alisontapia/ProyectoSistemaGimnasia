package edu.unl.ec.gimnasia.business.evaluation.service;

import edu.unl.ec.gimnasia.business.service.CrudGenericService;
import edu.unl.ec.gimnasia.domain.evaluation.Evaluation;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Stateless
public class EvaluationRepository implements Serializable {

    @Inject
    private CrudGenericService crudService;

    public Evaluation save(Evaluation evaluation) {
        if (evaluation.getId() == null) {
            return crudService.create(evaluation);
        }
        return crudService.update(evaluation);
    }

    public Evaluation findExisting(Long judgeId, Long gymnastId, Long competitionId, String criterion) {
        Map<String, Object> params = new HashMap<>();
        params.put("judgeId", judgeId);
        params.put("gymnastId", gymnastId);
        params.put("competitionId", competitionId);
        params.put("criterion", criterion);
        return crudService.findSingleResultOrNullWithNamedQuery("Evaluation.findExisting", params);
    }

    public List<Evaluation> findByJudgeGymnastCompetition(Long judgeId, Long gymnastId, Long competitionId) {
        Map<String, Object> params = new HashMap<>();
        params.put("judgeId", judgeId);
        params.put("gymnastId", gymnastId);
        params.put("competitionId", competitionId);
        return crudService.findWithNamedQuery("Evaluation.findByJudgeGymnastCompetition", params);
    }

    public List<Evaluation> findByGymnastAndCompetition(Long gymnastId, Long competitionId) {
        Map<String, Object> params = new HashMap<>();
        params.put("gymnastId", gymnastId);
        params.put("competitionId", competitionId);
        return crudService.findWithNamedQuery("Evaluation.findByGymnastAndCompetition", params);
    }

    public List<Evaluation> findByJudge(Long judgeId) {
        Map<String, Object> params = new HashMap<>();
        params.put("judgeId", judgeId);
        return crudService.findWithNamedQuery("Evaluation.findByJudge", params);
    }
}

