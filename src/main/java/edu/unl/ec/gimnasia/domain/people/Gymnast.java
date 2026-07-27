package edu.unl.ec.gimnasia.domain.people;

import edu.unl.ec.gimnasia.domain.competition.Category;
import edu.unl.ec.gimnasia.domain.competition.Competition;
import edu.unl.ec.gimnasia.domain.evaluation.Evaluation;
import edu.unl.ec.gimnasia.domain.evaluation.Result;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
/**
 * Representa una gimnasta dentro del sistema.
 *
 * Esta entidad hereda de Person para reutilizar los atributos y
 * comportamientos comunes de una persona, como identificación,
 * nombres y apellidos. Además, incorpora información específica
 * de las gimnastas, como fecha de nacimiento, categorías,
 * competiciones, evaluaciones y resultados obtenidos.
 */

@Entity
@Table(name = "gymnast")
@DiscriminatorValue("GYMNAST")
@NamedQueries({
        @NamedQuery(name = "Gymnast.findAll", query = "SELECT g FROM Gymnast g ORDER BY g.lastName, g.firstName"),
        @NamedQuery(name = "Gymnast.findByNationalId", query = "SELECT g FROM Gymnast g WHERE g.nationalId = :nationalId")
})
public class Gymnast extends Person {

    public static final int MIN_AGE = 4;
    public static final int MAX_AGE = 30;

    @NotNull
    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "registering_administrator_id")
    private Administrator registeringAdministrator;

    @ManyToMany(mappedBy = "participatingGymnasts")
    private final List<Competition> participatedCompetitions = new ArrayList<>();

    @ManyToMany(mappedBy = "gymnasts")
    private final List<Category> categories = new ArrayList<>();

    @OneToMany(mappedBy = "gymnast")
    private final List<Result> results = new ArrayList<>();

    @OneToMany(mappedBy = "gymnast")
    private final List<Evaluation> receivedEvaluations = new ArrayList<>();

    public Gymnast() {
        super();
    }

    public Gymnast(Long id, String nationalId, String firstName, String lastName, @NotNull LocalDate birthDate) {
        super(id, nationalId, firstName, lastName);
        this.birthDate = birthDate;
    }

    public int getAge() {
        return Period.between(birthDate, LocalDate.now()).getYears();
    }

    public String getFormattedBirthDate() {
        return birthDate == null ? "" : birthDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    public boolean isAgeValid() {
        int age = getAge();
        return age >= MIN_AGE && age <= MAX_AGE;
    }

    public boolean validateAge() {
        return isAgeValid();
    }

    public boolean matchesAgeRange(int minimumAge, int maximumAge) {
        int age = getAge();
        return age >= minimumAge && age <= maximumAge;
    }

    public void updateData(String firstName, String lastName, LocalDate birthDate) {
        setFirstName(firstName);
        setLastName(lastName);
        this.birthDate = birthDate;
    }

    public void setRegisteringAdministrator(Administrator administrator) {
        this.registeringAdministrator = administrator;
    }

    public void addCompetition(Competition competition) {
        if (!participatedCompetitions.contains(competition)) {
            participatedCompetitions.add(competition);
        }
    }

    public void addCategory(Category category) {
        if (!categories.contains(category)) {
            categories.add(category);
        }
    }

    public void addResult(Result result) {
        if (!results.contains(result)) {
            results.add(result);
        }
    }

    public void addEvaluation(Evaluation evaluation) {
        if (!receivedEvaluations.contains(evaluation)) {
            receivedEvaluations.add(evaluation);
        }
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public Administrator getRegisteringAdministrator() {
        return registeringAdministrator;
    }

    public List<Competition> getParticipatedCompetitions() {
        return participatedCompetitions;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public List<Result> getResults() {
        return results;
    }

    public List<Evaluation> getReceivedEvaluations() {
        return receivedEvaluations;
    }
}
