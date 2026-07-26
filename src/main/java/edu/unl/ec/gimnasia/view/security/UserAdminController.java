package edu.unl.ec.gimnasia.view.security;

import edu.unl.ec.gimnasia.business.security.SecurityFacade;
import edu.unl.ec.gimnasia.domain.people.Judge;
import edu.unl.ec.gimnasia.domain.security.Role;
import edu.unl.ec.gimnasia.domain.security.User;
import edu.unl.ec.gimnasia.exception.AlreadyEntityException;
import edu.unl.ec.gimnasia.exception.EncryptorException;
import edu.unl.ec.gimnasia.exception.EntityNotFoundException;
import edu.unl.ec.gimnasia.faces.FacesUtil;
import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;
import java.util.List;

@Named
@ViewScoped
public class UserAdminController implements Serializable {

    @Inject
    private SecurityFacade securityFacade;

    private List<User> users;
    private List<Role> availableRoles;
    private List<Judge> availableJudgesWithoutAccount;

    private String criteria;

    private Long editingId;
    private String username;
    private String password;
    private String roleName;
    private Long judgeId;

    @PostConstruct
    public void init() {
        availableRoles = securityFacade.findAllRoles();
        reload();
    }

    private void reload() {
        users = securityFacade.findUsers(criteria);
        availableJudgesWithoutAccount = securityFacade.findJudgesWithoutAccount();
    }

    public void search() {
        reload();
    }

    public void reset() {
        this.criteria = null;
        reload();
    }

    public void openNew() {
        editingId = null;
        username = null;
        password = null;
        roleName = null;
        judgeId = null;
        availableJudgesWithoutAccount = securityFacade.findJudgesWithoutAccount();
    }

    public void openEdit(User user) {
        password = null;
        if (user.hasRole(Role.ADMINISTRADOR)) {
            editingId = securityFacade.getSystemAdministrator().getId();
            roleName = Role.ADMINISTRADOR;
            username = user.getUsername();
            judgeId = null;
            return;
        }
        try {
            Judge judge = findJudgeByUsername(user.getUsername());
            editingId = judge.getId();
            roleName = Role.JUEZ;
            username = user.getUsername();
            judgeId = judge.getId();
        } catch (EntityNotFoundException e) {
            FacesUtil.addErrorMessage("No se pudo cargar la cuenta", e.getMessage());
        }
    }

    private Judge findJudgeByUsername(String username) throws EntityNotFoundException {
        return securityFacade.getSystemAdministrator().getRegisteredJudges().stream()
                .filter(Judge::hasAccount)
                .filter(j -> j.getUser().getUsername().equalsIgnoreCase(username))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Juez no encontrado para el usuario [" + username + "]"));
    }

    public void save() {
        try {
            if (Role.ADMINISTRADOR.equals(roleName)) {
                saveAdministrator();
            } else if (Role.JUEZ.equals(roleName)) {
                saveJudgeAccount();
            } else {
                FacesUtil.addErrorMessage("No se pudo guardar la cuenta", "Debe seleccionar un rol.");
                return;
            }
            reload();
        } catch (AlreadyEntityException | EncryptorException | EntityNotFoundException e) {
            FacesUtil.addErrorMessage("No se pudo guardar la cuenta", e.getMessage());
        }
    }

    private void saveAdministrator() throws EncryptorException {
        if (editingId == null) {
            FacesUtil.addErrorMessage("No se pudo guardar la cuenta",
                    "El sistema ya tiene un administrador; solo puede editar su cuenta existente.");
            return;
        }
        securityFacade.updateAdministratorAccount(username, password);
        FacesUtil.addSuccessMessage("Cuenta actualizada", username);
    }

    private void saveJudgeAccount() throws AlreadyEntityException, EncryptorException, EntityNotFoundException {
        if (editingId == null) {
            if (judgeId == null) {
                FacesUtil.addErrorMessage("No se pudo guardar la cuenta", "Debe elegir a que juez se le asigna la cuenta.");
                return;
            }
            securityFacade.assignJudgeAccount(judgeId, username, password);
            FacesUtil.addSuccessMessage("Cuenta asignada", username);
        } else {
            securityFacade.updateJudgeAccount(editingId, username, password);
            FacesUtil.addSuccessMessage("Cuenta actualizada", username);
        }
    }

    public String ownerName(User user) {
        if (user.hasRole(Role.ADMINISTRADOR)) {
            return securityFacade.getSystemAdministrator().getFullName();
        }
        if (user.hasRole(Role.JUEZ)) {
            try {
                return findJudgeByUsername(user.getUsername()).getFullName();
            } catch (EntityNotFoundException e) {
                return "-";
            }
        }
        return "-";
    }

    public String roleNamesOf(User user) {
        return user.getRoles().stream().map(Role::getName).reduce((a, b) -> a + ", " + b).orElse("-");
    }

    public boolean isEditing() {
        return editingId != null;
    }

    public boolean isJudgeRoleSelected() {
        return Role.JUEZ.equals(roleName);
    }

    public List<User> getUsers() {
        return users;
    }

    public List<Role> getAvailableRoles() {
        return availableRoles;
    }

    public List<Judge> getAvailableJudgesWithoutAccount() {
        return availableJudgesWithoutAccount;
    }

    public String getCriteria() {
        return criteria;
    }

    public void setCriteria(String criteria) {
        this.criteria = criteria;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public Long getJudgeId() {
        return judgeId;
    }

    public void setJudgeId(Long judgeId) {
        this.judgeId = judgeId;
    }
}
