package edu.unl.ec.gimnasia.view.judge;

import edu.unl.ec.gimnasia.business.evaluation.EvaluationFacade;
import edu.unl.ec.gimnasia.business.judge.JudgeFacade;
import edu.unl.ec.gimnasia.domain.competition.Competition;
import edu.unl.ec.gimnasia.domain.evaluation.Evaluation;
import edu.unl.ec.gimnasia.domain.people.Judge;
import edu.unl.ec.gimnasia.exception.CompetitionClosedException;
import edu.unl.ec.gimnasia.exception.EntityNotFoundException;
import edu.unl.ec.gimnasia.exception.InvalidScoreException;
import edu.unl.ec.gimnasia.faces.FacesUtil;
import edu.unl.ec.gimnasia.view.security.UserSessionBean;
import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Named
@ViewScoped
public class JudgeMyScoresController implements Serializable {

    @Inject
    private UserSessionBean userSessionBean;

    @Inject
    private JudgeFacade judgeFacade;

    @Inject
    private EvaluationFacade evaluationFacade;

    private Judge currentJudge;
    private List<JudgeScoreSummary> myScores;

    private List<Competition> competitionsInScores;
    private Long selectedCompetitionId;

    private JudgeScoreSummary selectedSummary;
    private Evaluation selectedEvaluation;
    private double editedValue;

    @PostConstruct
    public void init() {
        if (userSessionBean.getUser() == null) {
            FacesUtil.redirectToLogin();
            return;
        }
        try {
            currentJudge = judgeFacade.findByUser(userSessionBean.getUser().getUser());
            reload();
        } catch (EntityNotFoundException e) {
            FacesUtil.addErrorMessage("Error", e.getMessage());
        }
    }


    private void reload() {
        List<Evaluation> raw = evaluationFacade.findByJudge(currentJudge.getId());

        Map<String, List<Evaluation>> grouped = new LinkedHashMap<>();
        for (Evaluation evaluation : raw) {
            String key = evaluation.getCompetition().getId() + "-" + evaluation.getGymnast().getId();
            grouped.computeIfAbsent(key, k -> new ArrayList<>()).add(evaluation);
        }

        myScores = new ArrayList<>();
        for (List<Evaluation> group : grouped.values()) {
            double sum = group.stream().mapToDouble(Evaluation::getScore).sum();
            double finalScore = currentJudge.getSpecialty().calculateTotal(sum);
            Evaluation first = group.get(0);
            myScores.add(new JudgeScoreSummary(
                    first.getCompetition(), first.getCategory(), first.getGymnast(),
                    currentJudge.getSpecialty(), finalScore, group));
        }

        competitionsInScores = new ArrayList<>();
        for (JudgeScoreSummary summary : myScores) {
            if (!competitionsInScores.contains(summary.getCompetition())) {
                competitionsInScores.add(summary.getCompetition());
            }
        }
        if (selectedCompetitionId != null
                && competitionsInScores.stream().noneMatch(c -> c.getId().equals(selectedCompetitionId))) {
            selectedCompetitionId = null;
        }
        if (selectedCompetitionId == null && competitionsInScores.size() == 1) {
            selectedCompetitionId = competitionsInScores.get(0).getId();
        }
    }

    public List<JudgeScoreSummary> getScoresForSelectedCompetition() {
        if (selectedCompetitionId == null || myScores == null) {
            return List.of();
        }
        List<JudgeScoreSummary> filtered = new ArrayList<>();
        for (JudgeScoreSummary summary : myScores) {
            if (selectedCompetitionId.equals(summary.getCompetition().getId())) {
                filtered.add(summary);
            }
        }
        return filtered;
    }

    public boolean hasScores() {
        return myScores != null && !myScores.isEmpty();
    }

    public void onCompetitionChange() {
    }

    public void selectSummary(JudgeScoreSummary summary) {
        this.selectedSummary = summary;
        this.selectedEvaluation = null;
    }

    public void openEdit(Evaluation evaluation) {
        this.selectedEvaluation = evaluation;
        this.editedValue = evaluation.getScore();
    }

    public void saveEdit() {
        try {
            evaluationFacade.registerOrUpdateScore(
                    currentJudge.getId(),
                    selectedEvaluation.getGymnast().getId(),
                    selectedEvaluation.getCompetition().getId(),
                    selectedEvaluation.getCriterion(),
                    editedValue);
            FacesUtil.addSuccessMessage("Actualizado", "Calificación actualizada correctamente");
            reload();
        } catch (InvalidScoreException | EntityNotFoundException | CompetitionClosedException e) {
            FacesUtil.addErrorMessage("No se pudo actualizar", e.getMessage());
        }
    }

    public boolean isPenaltySpecialty() {
        return currentJudge != null && currentJudge.getSpecialty().isPenalty();
    }

    public Judge getCurrentJudge() {
        return currentJudge;
    }

    public List<JudgeScoreSummary> getMyScores() {
        return myScores;
    }

    public List<Competition> getCompetitionsInScores() {
        return competitionsInScores;
    }

    public Long getSelectedCompetitionId() {
        return selectedCompetitionId;
    }

    public void setSelectedCompetitionId(Long selectedCompetitionId) {
        this.selectedCompetitionId = selectedCompetitionId;
    }

    public JudgeScoreSummary getSelectedSummary() {
        return selectedSummary;
    }

    public Evaluation getSelectedEvaluation() {
        return selectedEvaluation;
    }

    public double getEditedValue() {
        return editedValue;
    }

    public void setEditedValue(double editedValue) {
        this.editedValue = editedValue;
    }
}
