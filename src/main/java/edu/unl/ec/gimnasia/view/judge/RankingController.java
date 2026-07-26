package edu.unl.ec.gimnasia.view.judge;

import edu.unl.ec.gimnasia.business.evaluation.EvaluationFacade;
import edu.unl.ec.gimnasia.domain.competition.Category;
import edu.unl.ec.gimnasia.domain.competition.Competition;
import edu.unl.ec.gimnasia.domain.competition.Specialty;
import edu.unl.ec.gimnasia.domain.evaluation.Result;
import edu.unl.ec.gimnasia.exception.EntityNotFoundException;
import edu.unl.ec.gimnasia.faces.FacesUtil;
import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Named
@ViewScoped
public class RankingController implements Serializable {

    @Inject
    private EvaluationFacade evaluationFacade;

    private List<Competition> competitions;
    private List<Category> categories;
    private List<Result> ranking;

    private Long selectedCompetitionId;
    private Long selectedCategoryId;

    @PostConstruct
    public void init() {
        competitions = evaluationFacade.findCompetitionsWithResults();
        categories = List.of();
        ranking = List.of();

        if (competitions.isEmpty()) {
            FacesUtil.addWarnMessage("Aviso", "Todavía no hay resultados disponibles en ninguna competencia.");
            return;
        }
        if (competitions.size() == 1) {
            selectedCompetitionId = competitions.get(0).getId();
            loadCategories();
        }
    }

    public void onCompetitionChange() {
        selectedCategoryId = null;
        ranking = List.of();
        loadCategories();
    }

    private void loadCategories() {
        if (selectedCompetitionId == null) {
            categories = List.of();
            return;
        }
        try {
            categories = evaluationFacade.findCategoriesWithResults(selectedCompetitionId);
            if (categories.isEmpty()) {
                FacesUtil.addWarnMessage("Aviso", "Esta competencia aún no tiene categorías con resultados disponibles.");
            }
        } catch (EntityNotFoundException e) {
            categories = List.of();
            FacesUtil.addWarnMessage("Aviso", e.getMessage());
        }
    }

    public void consultarRanking() {
        if (selectedCompetitionId == null) {
            FacesUtil.addWarnMessage("Aviso", "Selecciona una competencia antes de consultar el ranking.");
            return;
        }
        if (selectedCategoryId == null) {
            FacesUtil.addWarnMessage("Aviso", "Selecciona una categoría antes de consultar el ranking.");
            return;
        }
        ranking = evaluationFacade.generateRanking(selectedCompetitionId, selectedCategoryId);
        if (ranking.isEmpty()) {
            FacesUtil.addWarnMessage("Aviso", "Todavía no existen resultados disponibles para esta categoría.");
        }
    }

    public double totalOf(Result result, Specialty specialty) {
        Map<Specialty, Double> totals = result.totalsBySpecialty(result.getEvaluations());
        return totals.getOrDefault(specialty, 0.0);
    }

    public double totalDifficulty(Result result) {
        return totalOf(result, Specialty.DIFICULTAD);
    }

    public double totalExecution(Result result) {
        return totalOf(result, Specialty.EJECUCION);
    }

    public double totalArtistic(Result result) {
        return totalOf(result, Specialty.ARTISTICO);
    }

    public String estadoOf(Result result) {
        return result.getCompetition() != null && result.getCompetition().isClosed()
                ? "Cerrada" : "Abierta";
    }

    public boolean hasCompetitionsWithResults() {
        return competitions != null && !competitions.isEmpty();
    }

    public List<Competition> getCompetitions() {
        return competitions;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public List<Result> getRanking() {
        return ranking;
    }

    public Long getSelectedCompetitionId() {
        return selectedCompetitionId;
    }

    public void setSelectedCompetitionId(Long selectedCompetitionId) {
        this.selectedCompetitionId = selectedCompetitionId;
    }

    public Long getSelectedCategoryId() {
        return selectedCategoryId;
    }

    public void setSelectedCategoryId(Long selectedCategoryId) {
        this.selectedCategoryId = selectedCategoryId;
    }
}
