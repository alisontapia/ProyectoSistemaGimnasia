package edu.unl.ec.gimnasia.business.security;

import edu.unl.ec.gimnasia.business.administrator.service.AdministratorRepository;
import edu.unl.ec.gimnasia.business.judge.JudgeFacade;
import edu.unl.ec.gimnasia.business.judge.service.JudgeRepository;
import edu.unl.ec.gimnasia.business.security.service.RoleRepository;
import edu.unl.ec.gimnasia.domain.people.Administrator;
import edu.unl.ec.gimnasia.domain.people.Judge;
import edu.unl.ec.gimnasia.domain.security.Role;
import edu.unl.ec.gimnasia.domain.security.User;
import edu.unl.ec.gimnasia.exception.AlreadyEntityException;
import edu.unl.ec.gimnasia.exception.CredentialInvalidException;
import edu.unl.ec.gimnasia.exception.EncryptorException;
import edu.unl.ec.gimnasia.exception.EntityNotFoundException;
import edu.unl.ec.gimnasia.util.EncryptorManager;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Stateless
public class SecurityFacade implements Serializable {

    @Inject
    private AdministratorRepository administratorRepository;
    @Inject
    private JudgeRepository judgeRepository;
    @Inject
    private RoleRepository roleRepository;
    @Inject
    private JudgeFacade judgeFacade;

    public User authenticate(String username, String password) throws CredentialInvalidException {
        try {
            User userFound = findUserByUsername(username);
            if (userFound == null) {
                throw new CredentialInvalidException();
            }
            String encryptedInput = EncryptorManager.encrypt(password);
            if (userFound.getPassword().equals(encryptedInput)) {
                return userFound;
            }
            throw new CredentialInvalidException();
        } catch (EncryptorException e) {
            throw new CredentialInvalidException("No se pudo validar la contraseña", e);
        }
    }

    private User findUserByUsername(String username) {
        Administrator admin = administratorRepository.getSystemAdmin();
        if (admin.getUser().getUsername().equalsIgnoreCase(username)) {
            return admin.getUser();
        }
        for (Judge judge : judgeRepository.findAll()) {
            if (judge.hasAccount() && judge.getUser().getUsername().equalsIgnoreCase(username)) {
                return judge.getUser();
            }
        }
        return null;
    }

    public Administrator getSystemAdministrator() {
        return administratorRepository.getSystemAdmin();
    }

    public List<Role> findAllRoles() {
        return roleRepository.findAll();
    }

    public List<User> findUsers(String criteria) {
        String needle = criteria == null ? "" : criteria.trim().toLowerCase();
        List<User> found = new ArrayList<>();
        User adminUser = administratorRepository.getSystemAdmin().getUser();
        if (adminUser.getUsername().toLowerCase().contains(needle)) {
            found.add(adminUser);
        }
        for (Judge judge : judgeRepository.findAll()) {
            if (judge.hasAccount() && judge.getUser().getUsername().toLowerCase().contains(needle)) {
                found.add(judge.getUser());
            }
        }
        return found;
    }

    public List<Judge> findJudgesWithoutAccount() {
        return judgeFacade.findWithoutAccount();
    }

    public Judge assignJudgeAccount(Long judgeId, String username, String rawPassword)
            throws EntityNotFoundException, AlreadyEntityException, EncryptorException {
        return judgeFacade.assignAccount(judgeId, username, rawPassword);
    }

    public Judge updateJudgeAccount(Long judgeId, String username, String rawPasswordOrNull)
            throws EntityNotFoundException, AlreadyEntityException, EncryptorException {
        return judgeFacade.updateAccount(judgeId, username, rawPasswordOrNull);
    }

    public void changeAdministratorPassword(String rawPassword) throws EncryptorException {
        administratorRepository.getSystemAdmin().getUser().setPassword(EncryptorManager.encrypt(rawPassword));
    }

    public void updateAdministratorAccount(String username, String rawPasswordOrNull) throws EncryptorException {
        User adminUser = administratorRepository.getSystemAdmin().getUser();
        adminUser.setUsername(username);
        if (rawPasswordOrNull != null && !rawPasswordOrNull.isBlank()) {
            adminUser.setPassword(EncryptorManager.encrypt(rawPasswordOrNull));
        }
    }
}
