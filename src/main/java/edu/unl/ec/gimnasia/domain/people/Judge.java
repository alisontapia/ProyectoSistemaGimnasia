package edu.unl.ec.gimnasia.domain.people;

import edu.unl.ec.gimnasia.domain.competition.Competition;
import edu.unl.ec.gimnasia.domain.competition.Specialty;
import edu.unl.ec.gimnasia.domain.evaluation.Evaluation;
import edu.unl.ec.gimnasia.domain.security.User;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "judge")
@DiscriminatorValue("JUDGE")
@NamedQueries({
        @NamedQuery(name = "Judge.findAll", query = "SELECT j FROM Judge j ORDER BY j.lastName, j.firstName"),
        @NamedQuery(name = "Judge.findByUsername",
                query = "SELECT j FROM Judge j WHERE LOWER(j.user.username) = LOWER(:username)"),
        @NamedQuery(name = "Judge.findWithoutAccount", query = "SELECT j FROM Judge j WHERE j.user IS NULL")
})
public class Judge extends Person {

    @NotNull
    @Enumerated(EnumType.STRING)
    private Specialty specialty;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true)
    private User user;

    @ManyToMany(mappedBy = "assignedJudges")
    private final List<Competition> assignedCompetitions = new ArrayList<>();

    @OneToMany(mappedBy = "judge")
    private final List<Evaluation> issuedEvaluations = new ArrayList<>();

    public Judge() {
        super();
    }

    public Judge(Long id, String nationalId, String firstName, String lastName,
                 @NotNull Specialty specialty, User user) {
        super(id, nationalId, firstName, lastName);
        this.specialty = specialty;
        this.user = user;
    }

    public Judge(Long id, String nationalId, String firstName, String lastName, @NotNull Specialty specialty) {
        super(id, nationalId, firstName, lastName);
        this.specialty = specialty;
        this.user = null;
    }

    public boolean hasAccount() {
        return user != null;
    }

    public String[] getSpecialtyCriteria() {
        return specialty.getCriteria();
    }

    public void assignCompetition(Competition competition) {
        if (competition != null && !assignedCompetitions.contains(competition)) {
            assignedCompetitions.add(competition);
        }
    }

    public void unassignCompetition(Competition competition) {
        assignedCompetitions.remove(competition);
    }

    public void issueEvaluation(Evaluation evaluation) {
        if (!issuedEvaluations.contains(evaluation)) {
            issuedEvaluations.add(evaluation);
        }
    }

    public Specialty getSpecialty() {
        return specialty;
    }

    public void setSpecialty(Specialty specialty) {
        this.specialty = specialty;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<Competition> getAssignedCompetitions() {
        return assignedCompetitions;
    }

    public List<Evaluation> getIssuedEvaluations() {
        return issuedEvaluations;
    }
}
