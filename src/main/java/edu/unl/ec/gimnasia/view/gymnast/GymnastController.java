package edu.unl.ec.gimnasia.view.gymnast;

import edu.unl.ec.gimnasia.business.gymnast.GymnastFacade;
import edu.unl.ec.gimnasia.domain.people.Gymnast;
import edu.unl.ec.gimnasia.exception.AlreadyEntityException;
import edu.unl.ec.gimnasia.exception.EntityNotFoundException;
import edu.unl.ec.gimnasia.exception.InvalidAgeException;
import edu.unl.ec.gimnasia.faces.FacesUtil;
import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

/**
 * Controlador de la vista de gimnastas.
 *
 * Actúa como intermediario entre las páginas JSF y la capa de negocio,
 * gestionando las operaciones de registro, edición y consulta de
 * gimnastas. Además, administra los datos mostrados en la interfaz
 * y las notificaciones presentadas al usuario.
 */

@Named
@ViewScoped
public class GymnastController implements Serializable {

    @Inject
    private GymnastFacade gymnastFacade;

    private List<Gymnast> gymnasts;

    private Long editingId;
    private String nationalId;
    private String firstName;
    private String lastName;
    private LocalDate birthDate;

    @PostConstruct
    public void init() {
        reloadGymnasts();
    }

    private void reloadGymnasts() {
        gymnasts = gymnastFacade.findAll();
    }

    public void openNew() {
        editingId = null;
        nationalId = null;
        firstName = null;
        lastName = null;
        birthDate = null;
    }

    public void openEdit(Gymnast gymnast) {
        editingId = gymnast.getId();
        nationalId = gymnast.getNationalId();
        firstName = gymnast.getFirstName();
        lastName = gymnast.getLastName();
        birthDate = gymnast.getBirthDate();
    }

    public void save() {
        try {
            if (editingId == null) {
                gymnastFacade.register(nationalId, firstName, lastName, birthDate);
                FacesUtil.addSuccessMessage("Gimnasta registrada", firstName + " " + lastName);
            } else {
                gymnastFacade.update(editingId, firstName, lastName, birthDate);
                FacesUtil.addSuccessMessage("Gimnasta actualizada", firstName + " " + lastName);
            }
            reloadGymnasts();
        } catch (InvalidAgeException | AlreadyEntityException | EntityNotFoundException e) {
            FacesUtil.addErrorMessage("No se pudo guardar", e.getMessage());
        }
    }

    public boolean isEditing() {
        return editingId != null;
    }

    public List<Gymnast> getGymnasts() {
        return gymnasts;
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

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }
}
