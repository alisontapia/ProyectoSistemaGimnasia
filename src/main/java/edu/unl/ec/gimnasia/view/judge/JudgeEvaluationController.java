package edu.unl.ec.gimnasia.view.judge;

import edu.unl.ec.gimnasia.business.competition.CompetitionFacade;
import edu.unl.ec.gimnasia.business.evaluation.EvaluationFacade;
import edu.unl.ec.gimnasia.business.judge.JudgeFacade;
import edu.unl.ec.gimnasia.domain.competition.Competition;
import edu.unl.ec.gimnasia.domain.people.Gymnast;
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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Named
@ViewScoped
public class JudgeEvaluationController implements Serializable {

    @Inject
    private UserSessionBean userSessionBean;

    @Inject
    private JudgeFacade judgeFacade;

    @Inject
    private CompetitionFacade competitionFacade;

    @Inject
    private EvaluationFacade evaluationFacade;

    private Judge currentJudge;
    private Competition activeCompetition;
    private List<Gymnast> participants;

    private Gymnast selectedGymnast;
    private final Map<String, Double> scores = new LinkedHashMap<>();
    private Double judgeTotal;

    private Gymnast pendingGymnast;
    private boolean reopenedExisting;

    @PostConstruct
    public void init() {
        if (userSessionBean.getUser() == null) {
            FacesUtil.redirectToLogin();
            return;
        }
        participants = List.of();
        try {
            currentJudge = judgeFacade.findByUser(userSessionBean.getUser().getUser());
        } catch (EntityNotFoundException e) {
            currentJudge = null;
            FacesUtil.addErrorMessage("Error", e.getMessage());
            return;
        }
        try {
            activeCompetition = competitionFacade.findActive();
            participants = activeCompetition.getParticipatingGymnasts();
        } catch (EntityNotFoundException e) {
            activeCompetition = null;
            FacesUtil.addWarnMessage("Aviso", e.getMessage());
        }
    }

    private boolean refreshActiveCompetition() {
        Competition previous = activeCompetition;
        try {
            activeCompetition = competitionFacade.findActive();
        } catch (EntityNotFoundException e) {
            activeCompetition = null;
        }
        participants = activeCompetition != null ? activeCompetition.getParticipatingGymnasts() : List.of();
        return previous == null ? activeCompetition != null
                : !previous.equals(activeCompetition);
    }

    public void selectGymnast(Gymnast gymnast) {
        if (refreshActiveCompetition() || activeCompetition == null) {
            if (activeCompetition == null) {
                FacesUtil.addWarnMessage("Aviso", "El administrador ya no tiene ninguna competencia activa seleccionada.");
                return;
            }
            FacesUtil.addWarnMessage("Aviso", "La competencia activa cambio a \"" + activeCompetition.getName()
                    + "\". Se actualizo la lista de gimnastas; selecciona de nuevo.");
        }
        this.selectedGymnast = gymnast;
        this.judgeTotal = null;
        scores.clear();
        boolean foundExisting = false;
        for (String criterion : currentJudge.getSpecialtyCriteria()) {
            var existing = evaluationFacade.findExisting(
                    currentJudge.getId(), gymnast.getId(), activeCompetition.getId(), criterion);
            scores.put(criterion, existing != null ? existing.getScore() : 0.0);
            foundExisting = foundExisting || existing != null;
        }
        this.reopenedExisting = foundExisting;
    }

    public boolean isAlreadyEvaluated(Gymnast gymnast) {
        if (currentJudge == null || activeCompetition == null || gymnast == null) {
            return false;
        }
        for (String criterion : currentJudge.getSpecialtyCriteria()) {
            if (evaluationFacade.findExisting(currentJudge.getId(), gymnast.getId(),
                    activeCompetition.getId(), criterion) != null) {
                return true;
            }
        }
        return false;
    }

    public void prepareReEvaluate(Gymnast gymnast) {
        this.pendingGymnast = gymnast;
    }

    public void confirmReEvaluate() {
        if (pendingGymnast != null) {
            selectGymnast(pendingGymnast);
        }
    }

    public void handleCalificarClick(Gymnast gymnast) {
        boolean already = isAlreadyEvaluated(gymnast);
        if (already) {
            this.pendingGymnast = gymnast;
        } else {
            selectGymnast(gymnast);
        }
        org.primefaces.PrimeFaces.current().ajax().addCallbackParam("alreadyEvaluated", already);
    }

    public void saveScores() {

        refreshActiveCompetition();

        if (activeCompetition == null) {
            FacesUtil.addWarnMessage(
                    "Aviso",
                    "No existe una competencia activa."
            );
            return;
        }

        if (activeCompetition.isClosed()) {
            FacesUtil.addErrorMessage(
                    "Competencia cerrada",
                    "El administrador cerró esta competencia. Las calificaciones solo están disponibles para consulta."
            );
            return;
        }

        if (selectedGymnast == null) {
            FacesUtil.addWarnMessage(
                    "Aviso",
                    "Debe seleccionar una gimnasta antes de guardar."
            );
            return;
        }
        if (scores.isEmpty()) {
            FacesUtil.addWarnMessage("Aviso", "No hay criterios de evaluación para guardar.");
            return;
        }

        try {

            for (Map.Entry<String, Double> entry : scores.entrySet()) {

                evaluationFacade.registerOrUpdateScore(
                        currentJudge.getId(),
                        selectedGymnast.getId(),
                        activeCompetition.getId(),
                        entry.getKey(),
                        entry.getValue()
                );
            }

            judgeTotal = evaluationFacade.calculateJudgeTotal(
                    currentJudge.getId(),
                    selectedGymnast.getId(),
                    activeCompetition.getId()
            );

            FacesUtil.addSuccessMessage(
                    "Calificación guardada",
                    selectedGymnast.getFullName()
                            + " - Total "
                            + currentJudge.getSpecialty()
                            + ": "
                            + String.format("%.2f", judgeTotal)
            );

        } catch (InvalidScoreException |
                EntityNotFoundException |
                CompetitionClosedException e) {

            FacesUtil.addErrorMessage(
                    "No se pudo guardar la calificación",
                    e.getMessage()
            );
        }
    }
    public boolean isPenaltySpecialty() {
        return currentJudge != null && currentJudge.getSpecialty().isPenalty();
    }

    public boolean hasActiveCompetition() {

        try {
            activeCompetition = competitionFacade.findActive();
            participants = activeCompetition.getParticipatingGymnasts();
            return true;

        } catch (EntityNotFoundException e) {
            activeCompetition = null;
            participants = List.of();
            return false;
        }
    }
    public boolean isActiveCompetitionClosed() {

        try {
            activeCompetition = competitionFacade.findActive();
        } catch (EntityNotFoundException e) {
            activeCompetition = null;
            return false;
        }

        return activeCompetition != null
                && activeCompetition.isClosed();
    }

    public Judge getCurrentJudge() {
        return currentJudge;
    }

    public Competition getActiveCompetition() {
        return activeCompetition;
    }

    public List<Gymnast> getParticipants() {
        return participants;
    }

    public Gymnast getSelectedGymnast() {
        return selectedGymnast;
    }

    public Map<String, Double> getScores() {
        return scores;
    }

    public Double getJudgeTotal() {
        return judgeTotal;
    }

    public Gymnast getPendingGymnast() {
        return pendingGymnast;
    }

    public boolean isReopenedExisting() {
        return reopenedExisting;
    }
}
