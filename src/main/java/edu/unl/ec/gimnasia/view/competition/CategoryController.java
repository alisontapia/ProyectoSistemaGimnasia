package edu.unl.ec.gimnasia.view.competition;

import edu.unl.ec.gimnasia.business.competition.CompetitionFacade;
import edu.unl.ec.gimnasia.business.gymnast.GymnastFacade;
import edu.unl.ec.gimnasia.domain.competition.Category;
import edu.unl.ec.gimnasia.domain.competition.Competition;
import edu.unl.ec.gimnasia.domain.people.Gymnast;
import edu.unl.ec.gimnasia.exception.EntityNotFoundException;
import edu.unl.ec.gimnasia.exception.InvalidAgeException;
import edu.unl.ec.gimnasia.faces.FacesUtil;
import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;
import java.util.List;

@Named
@ViewScoped
public class CategoryController implements Serializable {

    @Inject
    private CompetitionFacade competitionFacade;

    @Inject
    private GymnastFacade gymnastFacade;

    private Competition activeCompetition;
    private List<Gymnast> allGymnasts;

    private String name;
    private String level;
    private int minimumAge;
    private int maximumAge;

    private Long selectedGymnastId;
    private Long selectedCategoryId;

    @PostConstruct
    public void init() {
        reload();
        allGymnasts = gymnastFacade.findAll();
    }

    private void reload() {
        try {
            activeCompetition = competitionFacade.findActive();
        } catch (EntityNotFoundException e) {
            activeCompetition = null;
        }
    }

    public boolean hasActiveCompetition() {
        return activeCompetition != null;
    }

    public void createCategory() {
        try {
            competitionFacade.createCategory(activeCompetition.getId(), name, level, minimumAge, maximumAge);
            FacesUtil.addSuccessMessage("Categoría creada", name);
            name = null;
            level = null;
            minimumAge = 0;
            maximumAge = 0;
            reload();
        } catch (EntityNotFoundException | InvalidAgeException e) {
            FacesUtil.addErrorMessage("No se pudo crear la categoría", e.getMessage());
        }
    }

    public void assignGymnast() {
        try {
            competitionFacade.assignGymnastToCategory(activeCompetition.getId(), selectedCategoryId, selectedGymnastId);
            FacesUtil.addSuccessMessage("Gimnasta asignada", "Asignación realizada correctamente");
            reload();
        } catch (EntityNotFoundException | InvalidAgeException e) {
            FacesUtil.addErrorMessage("No se pudo asignar", e.getMessage());
        }
    }

    public Competition getActiveCompetition() {
        return activeCompetition;
    }

    public List<Category> getCategories() {
        return activeCompetition == null ? List.of() : activeCompetition.getCategories();
    }

    public List<Gymnast> getAllGymnasts() {
        return allGymnasts;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public int getMinimumAge() {
        return minimumAge;
    }

    public void setMinimumAge(int minimumAge) {
        this.minimumAge = minimumAge;
    }

    public int getMaximumAge() {
        return maximumAge;
    }

    public void setMaximumAge(int maximumAge) {
        this.maximumAge = maximumAge;
    }

    public Long getSelectedGymnastId() {
        return selectedGymnastId;
    }

    public void setSelectedGymnastId(Long selectedGymnastId) {
        this.selectedGymnastId = selectedGymnastId;
    }

    public Long getSelectedCategoryId() {
        return selectedCategoryId;
    }

    public void setSelectedCategoryId(Long selectedCategoryId) {
        this.selectedCategoryId = selectedCategoryId;
    }
}
