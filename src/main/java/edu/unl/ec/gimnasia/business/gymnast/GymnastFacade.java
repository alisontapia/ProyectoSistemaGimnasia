package edu.unl.ec.gimnasia.business.gymnast;

import edu.unl.ec.gimnasia.business.administrator.service.AdministratorRepository;
import edu.unl.ec.gimnasia.business.gymnast.service.GymnastRepository;
import edu.unl.ec.gimnasia.domain.people.Administrator;
import edu.unl.ec.gimnasia.domain.people.Gymnast;
import edu.unl.ec.gimnasia.exception.AlreadyEntityException;
import edu.unl.ec.gimnasia.exception.EntityNotFoundException;
import edu.unl.ec.gimnasia.exception.InvalidAgeException;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

@Stateless
public class GymnastFacade implements Serializable {

    @Inject
    private GymnastRepository gymnastRepository;
    @Inject
    private AdministratorRepository administratorRepository;

    public Gymnast register(String nationalId, String firstName, String lastName, LocalDate birthDate)
            throws InvalidAgeException, AlreadyEntityException {
        Gymnast gymnast = new Gymnast(null, nationalId, firstName, lastName, birthDate);
        if (!gymnast.isAgeValid()) {
            throw new InvalidAgeException("La edad de la gimnasta debe estar entre "
                    + Gymnast.MIN_AGE + " y " + Gymnast.MAX_AGE + " años (edad actual: "
                    + gymnast.getAge() + ").");
        }
        Gymnast saved = gymnastRepository.save(gymnast);
        Administrator admin = administratorRepository.getSystemAdmin();
        admin.registerGymnast(saved);
        return saved;
    }

    public Gymnast update(Long id, String firstName, String lastName, LocalDate birthDate)
            throws EntityNotFoundException, InvalidAgeException, AlreadyEntityException {
        Gymnast gymnast = gymnastRepository.find(id);
        gymnast.updateData(firstName, lastName, birthDate);
        if (!gymnast.isAgeValid()) {
            throw new InvalidAgeException("La edad resultante debe estar entre "
                    + Gymnast.MIN_AGE + " y " + Gymnast.MAX_AGE + " años.");
        }
        return gymnastRepository.save(gymnast);
    }

    public Gymnast findById(Long id) throws EntityNotFoundException {
        return gymnastRepository.find(id);
    }

    public List<Gymnast> findAll() {
        return gymnastRepository.findAll();
    }
}
