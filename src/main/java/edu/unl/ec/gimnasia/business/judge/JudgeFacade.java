package edu.unl.ec.gimnasia.business.judge;

import edu.unl.ec.gimnasia.business.administrator.service.AdministratorRepository;
import edu.unl.ec.gimnasia.business.judge.service.JudgeRepository;
import edu.unl.ec.gimnasia.business.security.service.RoleRepository;
import edu.unl.ec.gimnasia.domain.competition.Specialty;
import edu.unl.ec.gimnasia.domain.people.Judge;
import edu.unl.ec.gimnasia.domain.security.Role;
import edu.unl.ec.gimnasia.domain.security.User;
import edu.unl.ec.gimnasia.exception.AlreadyEntityException;
import edu.unl.ec.gimnasia.exception.EncryptorException;
import edu.unl.ec.gimnasia.exception.EntityInUseException;
import edu.unl.ec.gimnasia.exception.EntityNotFoundException;
import edu.unl.ec.gimnasia.util.EncryptorManager;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

@Stateless
public class JudgeFacade implements Serializable {

    @Inject
    private JudgeRepository judgeRepository;
    @Inject
    private RoleRepository roleRepository;
    @Inject
    private AdministratorRepository administratorRepository;

    public Judge findById(Long id) throws EntityNotFoundException {
        return judgeRepository.find(id);
    }

    public Judge findByUser(User user) throws EntityNotFoundException {
        return judgeRepository.findByUsername(user.getUsername());
    }

    public List<Judge> findAll() {
        return judgeRepository.findAll();
    }

    public List<Judge> findWithoutAccount() {
        return judgeRepository.findWithoutAccount();
    }

    public Judge create(String nationalId, String firstName, String lastName, Specialty specialty)
            throws AlreadyEntityException {
        assertNationalIdAvailable(nationalId, null);
        Judge judge = judgeRepository.save(new Judge(null, nationalId, firstName, lastName, specialty));
        administratorRepository.getSystemAdmin().registerJudge(judge);
        return judge;
    }

    public Judge update(Long id, String nationalId, String firstName, String lastName, Specialty specialty)
            throws EntityNotFoundException, AlreadyEntityException {
        Judge judge = judgeRepository.find(id);
        assertNationalIdAvailable(nationalId, id);
        judge.setNationalId(nationalId);
        judge.setFirstName(firstName);
        judge.setLastName(lastName);
        judge.setSpecialty(specialty);
        return judgeRepository.save(judge);
    }

    public void delete(Long id) throws EntityNotFoundException, EntityInUseException {
        Judge judge = judgeRepository.find(id);
        if (!judge.getIssuedEvaluations().isEmpty()) {
            throw new EntityInUseException("No se puede eliminar a " + judge.getFullName()
                    + ": ya tiene calificaciones registradas.");
        }
        if (!judge.getAssignedCompetitions().isEmpty()) {
            throw new EntityInUseException("No se puede eliminar a " + judge.getFullName()
                    + ": esta asignado a una o más competencias.");
        }
        judgeRepository.delete(id);
    }

    private void assertNationalIdAvailable(String nationalId, Long ownIdOrNull) throws AlreadyEntityException {
        boolean taken = judgeRepository.findAll().stream()
                .anyMatch(j -> j.getNationalId().equals(nationalId) && !Objects.equals(j.getId(), ownIdOrNull));
        if (taken) {
            throw new AlreadyEntityException("Ya existe un juez registrado con la cedula [" + nationalId + "]");
        }
    }


    public Judge assignAccount(Long judgeId, String username, String rawPassword)
            throws EntityNotFoundException, AlreadyEntityException, EncryptorException {
        Judge judge = judgeRepository.find(judgeId);
        if (judge.hasAccount()) {
            throw new AlreadyEntityException(judge.getFullName() + " ya tiene una cuenta asignada.");
        }
        assertUsernameAvailable(username, null);
        judge.setUser(new User(null, username, EncryptorManager.encrypt(rawPassword),
                roleRepository.find(Role.JUEZ)));
        return judgeRepository.save(judge);
    }

    public Judge updateAccount(Long judgeId, String username, String rawPasswordOrNull)
            throws EntityNotFoundException, AlreadyEntityException, EncryptorException {
        Judge judge = judgeRepository.find(judgeId);
        if (!judge.hasAccount()) {
            throw new EntityNotFoundException(judge.getFullName() + " todavia no tiene una cuenta asignada.");
        }
        assertUsernameAvailable(username, judgeId);
        judge.getUser().setUsername(username);
        if (rawPasswordOrNull != null && !rawPasswordOrNull.isBlank()) {
            judge.getUser().setPassword(EncryptorManager.encrypt(rawPasswordOrNull));
        }
        return judgeRepository.save(judge);
    }

    private void assertUsernameAvailable(String username, Long ownIdOrNull) throws AlreadyEntityException {
        try {
            Judge existing = judgeRepository.findByUsername(username);
            if (!Objects.equals(existing.getId(), ownIdOrNull)) {
                throw new AlreadyEntityException("Ya existe otra cuenta con el nombre de usuario [" + username + "]");
            }
        } catch (EntityNotFoundException e) {
            // Username disponible: no existe ningún juez registrado con ese nombre
        }
    }
}
