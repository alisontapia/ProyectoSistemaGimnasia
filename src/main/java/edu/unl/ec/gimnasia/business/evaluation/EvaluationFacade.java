package edu.unl.ec.gimnasia.business.evaluation;

import edu.unl.ec.gimnasia.business.competition.service.CompetitionRepository;
import edu.unl.ec.gimnasia.business.evaluation.service.EvaluationRepository;
import edu.unl.ec.gimnasia.business.evaluation.service.ResultRepository;
import edu.unl.ec.gimnasia.business.gymnast.service.GymnastRepository;
import edu.unl.ec.gimnasia.business.judge.service.JudgeRepository;
import edu.unl.ec.gimnasia.domain.competition.Category;
import edu.unl.ec.gimnasia.domain.competition.Competition;
import edu.unl.ec.gimnasia.domain.evaluation.Evaluation;
import edu.unl.ec.gimnasia.domain.evaluation.Result;
import edu.unl.ec.gimnasia.domain.people.Gymnast;
import edu.unl.ec.gimnasia.domain.people.Judge;
import edu.unl.ec.gimnasia.exception.CompetitionClosedException;
import edu.unl.ec.gimnasia.exception.EntityNotFoundException;
import edu.unl.ec.gimnasia.exception.InvalidScoreException;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Stateless
public class EvaluationFacade implements Serializable {

    @Inject
    private EvaluationRepository evaluationRepository;
    @Inject
    private ResultRepository resultRepository;
    @Inject
    private JudgeRepository judgeRepository;
    @Inject
    private GymnastRepository gymnastRepository;
    @Inject
    private CompetitionRepository competitionRepository;

    public Evaluation registerOrUpdateScore(Long judgeId, Long gymnastId, Long competitionId,
                                             String criterion, double value)
            throws EntityNotFoundException, InvalidScoreException, CompetitionClosedException {
        Judge judge = judgeRepository.find(judgeId);
        Gymnast gymnast = gymnastRepository.find(gymnastId);
        Competition competition = competitionRepository.find(competitionId);

        if (competition.isClosed()) {
            throw new CompetitionClosedException("La competencia [" + competition.getName()
                    + "] ya fue cerrada por el administrador: las calificaciones quedan solo para consulta.");
        }

        Evaluation existing = evaluationRepository.findExisting(judgeId, gymnastId, competitionId, criterion);
        Evaluation evaluation;
        if (existing != null) {
            existing.update(value);
            evaluation = existing;
        } else {
            Category category = findCategoryOf(gymnast, competition);
            evaluation = new Evaluation(null, criterion, value, judge, gymnast, competition, category);
            evaluationRepository.save(evaluation);
        }

        tryGenerateResult(gymnastId, competitionId);
        return evaluation;
    }

    public Evaluation findExisting(Long judgeId, Long gymnastId, Long competitionId, String criterion) {
        return evaluationRepository.findExisting(judgeId, gymnastId, competitionId, criterion);
    }

    public List<Evaluation> findByJudge(Long judgeId) {
        return evaluationRepository.findByJudge(judgeId);
    }

    public double calculateJudgeTotal(Long judgeId, Long gymnastId, Long competitionId) throws EntityNotFoundException {
        Judge judge = judgeRepository.find(judgeId);
        List<Evaluation> evaluations = evaluationRepository.findByJudgeGymnastCompetition(judgeId, gymnastId, competitionId);
        double sum = evaluations.stream().mapToDouble(Evaluation::getScore).sum();
        return judge.getSpecialty().calculateTotal(sum);
    }

    public Result tryGenerateResult(Long gymnastId, Long competitionId) throws EntityNotFoundException {
        List<Evaluation> evaluations = evaluationRepository.findByGymnastAndCompetition(gymnastId, competitionId);
        if (!Result.hasAllSpecialties(evaluations)) {
            return null;
        }
        Gymnast gymnast = gymnastRepository.find(gymnastId);
        Competition competition = competitionRepository.find(competitionId);

        Result result = resultRepository.findByGymnastAndCompetition(gymnastId, competitionId);
        if (result == null) {
            result = new Result(null, gymnast, competition);
        }
        result.recalculate(evaluations);
        Result saved = resultRepository.save(result);
        gymnast.addResult(saved);
        competition.addResult(saved);
        return saved;
    }

    public List<Result> generateRanking(Long competitionId, Long categoryId) {
        List<Result> ranking = resultRepository.findAllByCompetition(competitionId).stream()
                .filter(r -> r.getCategory() != null && Objects.equals(r.getCategory().getId(), categoryId))
                .collect(java.util.stream.Collectors.toCollection(ArrayList::new));
        ranking.sort(Comparator.comparingDouble(Result::getFinalScore).reversed());
        for (int i = 0; i < ranking.size(); i++) {
            ranking.get(i).assignPosition(i + 1);
        }
        return ranking;
    }

    public List<Competition> findCompetitionsWithResults() {
        List<Competition> withResults = new ArrayList<>();
        for (Competition competition : competitionRepository.findAll()) {
            if (!resultRepository.findAllByCompetition(competition.getId()).isEmpty()) {
                withResults.add(competition);
            }
        }
        return withResults;
    }

    public List<Category> findCategoriesWithResults(Long competitionId) throws EntityNotFoundException {
        Competition competition = competitionRepository.find(competitionId);
        List<Result> results = resultRepository.findAllByCompetition(competitionId);
        List<Category> withResults = new ArrayList<>();
        for (Category category : competition.getCategories()) {
            boolean hasResult = results.stream()
                    .anyMatch(r -> r.getCategory() != null && Objects.equals(r.getCategory().getId(), category.getId()));
            if (hasResult) {
                withResults.add(category);
            }
        }
        return withResults;
    }

    private Category findCategoryOf(Gymnast gymnast, Competition competition) {
        Optional<Category> category = competition.getCategories().stream()
                .filter(c -> c.getGymnasts().contains(gymnast))
                .findFirst();
        return category.orElse(null);
    }
}

