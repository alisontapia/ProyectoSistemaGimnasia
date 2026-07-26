package edu.unl.ec.gimnasia.view.competition;

import edu.unl.ec.gimnasia.business.competition.CompetitionFacade;
import edu.unl.ec.gimnasia.business.judge.JudgeFacade;
import edu.unl.ec.gimnasia.domain.competition.Competition;
import edu.unl.ec.gimnasia.domain.competition.Specialty;
import edu.unl.ec.gimnasia.domain.people.Judge;
import edu.unl.ec.gimnasia.exception.EntityNotFoundException;
import edu.unl.ec.gimnasia.exception.InvalidJudgeAssignmentException;
import edu.unl.ec.gimnasia.faces.FacesUtil;
import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Named
@ViewScoped
public class CompetitionController implements Serializable {

    @Inject
    private CompetitionFacade competitionFacade;

    @Inject
    private JudgeFacade judgeFacade;

    private List<Competition> competitions;
    private List<Judge> availableJudges;
    private Long activeCompetitionId;

    private String name;
    private LocalDate date;
    private String location;
    private Long difficultyJudgeId;
    private Long executionJudgeId;
    private Long artisticJudgeId;

    private Long editingId;

    @PostConstruct
    public void init() {
        reload();
        availableJudges = judgeFacade.findAll();
    }

    private List<Judge> judgesOf(Specialty specialty) {
        List<Judge> result = new ArrayList<>();
        for (Judge judge : availableJudges) {
            if (judge.getSpecialty() == specialty) {
                result.add(judge);
            }
        }
        return result;
    }

    public List<Judge> getDifficultyJudges() {
        return judgesOf(Specialty.DIFICULTAD);
    }

    public List<Judge> getExecutionJudges() {
        return judgesOf(Specialty.EJECUCION);
    }

    public List<Judge> getArtisticJudges() {
        return judgesOf(Specialty.ARTISTICO);
    }

    private void reload() {
        competitions = competitionFacade.findAll();
        try {
            activeCompetitionId = competitionFacade.findActive().getId();
        } catch (EntityNotFoundException e) {
            activeCompetitionId = null;
        }
    }

    public void openNew() {
        editingId = null;
        name = null;
        date = null;
        location = null;
        difficultyJudgeId = null;
        executionJudgeId = null;
        artisticJudgeId = null;
    }

    public void openEdit(Competition competition) {
        editingId = competition.getId();
        name = competition.getName();
        date = competition.getDate();
        location = competition.getLocation();
        difficultyJudgeId = null;
        executionJudgeId = null;
        artisticJudgeId = null;
        for (Judge judge : competition.getAssignedJudges()) {
            switch (judge.getSpecialty()) {
                case DIFICULTAD -> difficultyJudgeId = judge.getId();
                case EJECUCION -> executionJudgeId = judge.getId();
                case ARTISTICO -> artisticJudgeId = judge.getId();
            }
        }
    }

    public boolean isEditing() {
        return editingId != null;
    }

    public void save() {
        try {
            if (editingId != null) {
                Competition competition = competitionFacade.update(editingId, name, date, location,
                        difficultyJudgeId, executionJudgeId, artisticJudgeId);
                FacesUtil.addSuccessMessage(" Competencia actualizada", competition.getName());
            } else {
                Competition competition = competitionFacade.create(name, date, location,
                        difficultyJudgeId, executionJudgeId, artisticJudgeId);
                FacesUtil.addSuccessMessage(" Competencia creada", competition.getName());
            }
            reload();
        } catch (EntityNotFoundException | InvalidJudgeAssignmentException e) {
            FacesUtil.addErrorMessage(" No se pudo guardar la competencia", e.getMessage());
        }
    }

    private Competition pendingActivation;

    public void selectForActivation(Competition competition) {
        this.pendingActivation = competition;
    }

    public void confirmActivate() {
        if (pendingActivation != null) {
            activate(pendingActivation);
        }
    }

    public void activate(Competition competition) {
        try {
            competitionFacade.setActive(competition.getId());
            FacesUtil.addSuccessMessage(" Competencia activa", competition.getName());
            reload();
        } catch (EntityNotFoundException e) {
            FacesUtil.addErrorMessage(" No se pudo activar", e.getMessage());
        }
    }

    public boolean isActive(Competition competition) {
        return activeCompetitionId != null && activeCompetitionId.equals(competition.getId());
    }

    private Competition pendingClosing;

    public void selectForClosing(Competition competition) {
        this.pendingClosing = competition;
    }

    public String getClosingConfirmationMessage() {
        if (pendingClosing == null) {
            return "";
        }
        if (isActive(pendingClosing)) {
            return "\"" + pendingClosing.getName() + "\" es la competencia ACTIVA en este momento. "
                    + " Si la cierras, los jueces dejaran de poder registrar calificaciones de inmediato "
                    + "(solo podran consultar lo ya guardado). Deseas continuar?";
        }
        return " Vas a cerrar \"" + pendingClosing.getName() + "\": las calificaciones quedarán solo para consulta. "
                + "Deseas continuar?";
    }

    public void confirmClose() {
        if (pendingClosing != null) {
            close(pendingClosing);
            pendingClosing = null;
        }
    }

    public void close(Competition competition) {
        try {
            competitionFacade.close(competition.getId());
            FacesUtil.addSuccessMessage(" Competencia cerrada",
                    competition.getName() + " - las calificaciones quedan solo para consulta.");
            reload();
        } catch (EntityNotFoundException e) {
            FacesUtil.addErrorMessage(" No se pudo cerrar", e.getMessage());
        }
    }

    public void reopen(Competition competition) {
        try {
            competitionFacade.reopen(competition.getId());
            FacesUtil.addSuccessMessage(" Competencia reabierta", competition.getName());
            reload();
        } catch (EntityNotFoundException e) {
            FacesUtil.addErrorMessage(" No se pudo reabrir", e.getMessage());
        }
    }

    public List<Competition> getCompetitions() {
        return competitions;
    }

    public List<Judge> getAvailableJudges() {
        return availableJudges;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Long getDifficultyJudgeId() {
        return difficultyJudgeId;
    }

    public void setDifficultyJudgeId(Long difficultyJudgeId) {
        this.difficultyJudgeId = difficultyJudgeId;
    }

    public Long getExecutionJudgeId() {
        return executionJudgeId;
    }

    public void setExecutionJudgeId(Long executionJudgeId) {
        this.executionJudgeId = executionJudgeId;
    }

    public Long getArtisticJudgeId() {
        return artisticJudgeId;
    }

    public void setArtisticJudgeId(Long artisticJudgeId) {
        this.artisticJudgeId = artisticJudgeId;
    }
}
