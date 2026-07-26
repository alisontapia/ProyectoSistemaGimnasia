package edu.unl.ec.gimnasia.view.judge;

import edu.unl.ec.gimnasia.business.judge.JudgeFacade;
import edu.unl.ec.gimnasia.domain.competition.Specialty;
import edu.unl.ec.gimnasia.domain.people.Judge;
import edu.unl.ec.gimnasia.exception.AlreadyEntityException;
import edu.unl.ec.gimnasia.exception.EntityInUseException;
import edu.unl.ec.gimnasia.exception.EntityNotFoundException;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import edu.unl.ec.gimnasia.faces.FacesUtil;
import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;
import java.util.List;

@Named
@ViewScoped
public class JudgeAdminController implements Serializable {

    @Inject
    private JudgeFacade judgeFacade;

    private List<Judge> judges;
    private final Specialty[] availableSpecialties = Specialty.values();

    private Long editingId;
    private String nationalId;
    private String firstName;
    private String lastName;
    private Specialty specialty;

    @PostConstruct
    public void init() {
        reload();
    }

    private void reload() {
        judges = judgeFacade.findAll();
    }

    public void openNew() {
        editingId = null;
        nationalId = null;
        firstName = null;
        lastName = null;
        specialty = null;
    }

    public void openEdit(Judge judge) {
        editingId = judge.getId();
        nationalId = judge.getNationalId();
        firstName = judge.getFirstName();
        lastName = judge.getLastName();
        specialty = judge.getSpecialty();
    }

    public void save() {
        try {
            if (editingId == null) {
                judgeFacade.create(nationalId, firstName, lastName, specialty);
                FacesUtil.addSuccessMessage("Juez registrado", firstName + " " + lastName);
            } else {
                judgeFacade.update(editingId, nationalId, firstName, lastName, specialty);
                FacesUtil.addSuccessMessage("Juez actualizado", firstName + " " + lastName);
            }
            reload();
        } catch (AlreadyEntityException e) {

            FacesContext.getCurrentInstance().addMessage("judgeForm:judgeDialog:nationalId",
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Cédula ya registrada", e.getMessage()));
        } catch (EntityNotFoundException e) {
            FacesUtil.addErrorMessage("No se pudo guardar", e.getMessage());
        }
    }

    public void delete(Judge judge) {
        try {
            judgeFacade.delete(judge.getId());
            FacesUtil.addSuccessMessage("Juez eliminado", judge.getFullName());
            reload();
        } catch (EntityNotFoundException | EntityInUseException e) {
            FacesUtil.addErrorMessage("No se pudo eliminar", e.getMessage());
        }
    }

    public boolean isEditing() {
        return editingId != null;
    }

    public List<Judge> getJudges() {
        return judges;
    }

    public Specialty[] getAvailableSpecialties() {
        return availableSpecialties;
    }

    public String getNationalId() {
        return nationalId;
    }

    public void setNationalId(String nationalId) {
        this.nationalId = nationalId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Specialty getSpecialty() {
        return specialty;
    }

    public void setSpecialty(Specialty specialty) {
        this.specialty = specialty;
    }
}
