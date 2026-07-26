package edu.unl.ec.gimnasia.business.competition;

import edu.unl.ec.gimnasia.business.administrator.service.AdministratorRepository;
import edu.unl.ec.gimnasia.business.competition.service.CompetitionRepository;
import edu.unl.ec.gimnasia.business.gymnast.service.GymnastRepository;
import edu.unl.ec.gimnasia.business.judge.service.JudgeRepository;
import edu.unl.ec.gimnasia.domain.competition.Category;
import edu.unl.ec.gimnasia.domain.competition.Competition;
import edu.unl.ec.gimnasia.domain.competition.Specialty;
import edu.unl.ec.gimnasia.domain.people.Administrator;
import edu.unl.ec.gimnasia.domain.people.Gymnast;
import edu.unl.ec.gimnasia.domain.people.Judge;
import edu.unl.ec.gimnasia.exception.EntityNotFoundException;
import edu.unl.ec.gimnasia.exception.InvalidAgeException;
import edu.unl.ec.gimnasia.exception.InvalidJudgeAssignmentException;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

@Stateless
public class CompetitionFacade implements Serializable {

    @Inject
    private CompetitionRepository competitionRepository;
    @Inject
    private JudgeRepository judgeRepository;
    @Inject
    private GymnastRepository gymnastRepository;
    @Inject
    private AdministratorRepository administratorRepository;

    public Competition create(String name, LocalDate date, String location,
                               Long difficultyJudgeId, Long executionJudgeId, Long artisticJudgeId)
            throws EntityNotFoundException, InvalidJudgeAssignmentException {
        Administrator admin = administratorRepository.getSystemAdmin();
        Competition competition = new Competition(null, name, date, location);
        admin.createCompetition(competition);

        assignJudgePanel(competition, difficultyJudgeId, executionJudgeId, artisticJudgeId);
        return competitionRepository.save(competition);
    }

    public Competition update(Long competitionId, String name, LocalDate date, String location,
                               Long difficultyJudgeId, Long executionJudgeId, Long artisticJudgeId)
            throws EntityNotFoundException, InvalidJudgeAssignmentException {
        Competition competition = competitionRepository.find(competitionId);
        competition.setName(name);
        competition.setDate(date);
        competition.setLocation(location);

        List<Judge> currentlyAssigned = new java.util.ArrayList<>(competition.getAssignedJudges());
        for (Judge judge : currentlyAssigned) {
            competition.removeJudge(judge);
        }
        assignJudgePanel(competition, difficultyJudgeId, executionJudgeId, artisticJudgeId);
        return competitionRepository.save(competition);
    }

    private void assignJudgePanel(Competition competition, Long difficultyJudgeId,
                                   Long executionJudgeId, Long artisticJudgeId)
            throws EntityNotFoundException, InvalidJudgeAssignmentException {
        competition.addJudge(findJudgeForSpecialty(difficultyJudgeId, Specialty.DIFICULTAD));
        competition.addJudge(findJudgeForSpecialty(executionJudgeId, Specialty.EJECUCION));
        competition.addJudge(findJudgeForSpecialty(artisticJudgeId, Specialty.ARTISTICO));
    }

    private Judge findJudgeForSpecialty(Long judgeId, Specialty expectedSpecialty)
            throws EntityNotFoundException, InvalidJudgeAssignmentException {
        if (judgeId == null) {
            throw new InvalidJudgeAssignmentException(
                    " Debe asignar un juez de " + expectedSpecialty + " a la competencia ");
        }
        Judge judge = judgeRepository.find(judgeId);
        if (judge.getSpecialty() != expectedSpecialty) {
            throw new InvalidJudgeAssignmentException(
                    " El juez " + judge.getFullName() + " es de " + judge.getSpecialty()
                            + " y no puede ocupar el cupo de " + expectedSpecialty + ".");
        }
        return judge;
    }

    public void setActive(Long competitionId) throws EntityNotFoundException {
        Competition competition = competitionRepository.find(competitionId);
        administratorRepository.getSystemAdmin().selectActiveCompetition(competition);
    }

    public Competition findActive() throws EntityNotFoundException {
        Competition active = administratorRepository.getSystemAdmin().getActiveCompetition();
        if (active == null) {
            throw new EntityNotFoundException(" No hay ninguna competencia activa seleccionada ");
        }
        return active;
    }

    public Competition findById(Long id) throws EntityNotFoundException {
        return competitionRepository.find(id);
    }

    public void close(Long competitionId) throws EntityNotFoundException {
        Competition competition = competitionRepository.find(competitionId);
        competition.close();
        competitionRepository.save(competition);

        Administrator admin = administratorRepository.getSystemAdmin();
        if (admin.getActiveCompetition() != null
                && admin.getActiveCompetition().getId().equals(competitionId)) {
            admin.selectActiveCompetition(competition);
        }
    }

    public void reopen(Long competitionId) throws EntityNotFoundException {
        Competition competition = competitionRepository.find(competitionId);
        competition.reopen();
        competitionRepository.save(competition);

        Administrator admin = administratorRepository.getSystemAdmin();
        if (admin.getActiveCompetition() != null
                && admin.getActiveCompetition().getId().equals(competitionId)) {
            admin.selectActiveCompetition(competition);
        }
    }

    public List<Competition> findAll() {
        return competitionRepository.findAll();
    }

    public Category createCategory(Long competitionId, String name, String level, int minimumAge, int maximumAge)
            throws EntityNotFoundException, InvalidAgeException {
        Competition competition = competitionRepository.find(competitionId);
        if (minimumAge <= 0 || maximumAge <= 0) {
            throw new InvalidAgeException(" La edad mínima y la edad máxima deben ser mayores a 0 ");
        }
        if (minimumAge > maximumAge) {
            throw new InvalidAgeException(" La edad mínima no puede ser mayor a la edad máxima ");
        }
        Category category = new Category(null, name, level, minimumAge, maximumAge);
        competition.addCategory(category);
        return category;
    }

    public void assignGymnastToCategory(Long competitionId, Long categoryId, Long gymnastId)
            throws EntityNotFoundException, InvalidAgeException {
        Competition competition = competitionRepository.find(competitionId);
        Gymnast gymnast = gymnastRepository.find(gymnastId);
        Category category = competition.getCategories().stream()
                .filter(c -> c.getId().equals(categoryId))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException(" Categoría no encontrada con id [" + categoryId + "]"));

        boolean assigned = competition.addGymnastToCategory(gymnast, category);
        if (!assigned) {
            throw new InvalidAgeException(" La edad de " + gymnast.getFullName() + " (" + gymnast.getAge()
                    + " años) no corresponde al rango " + category.getMinimumAge() + "-"
                    + category.getMaximumAge() + " de la categoría " + category.getName() );
        }
    }
}
