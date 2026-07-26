package edu.unl.ec.gimnasia.domain.people;

import edu.unl.ec.gimnasia.domain.competition.Competition;
import edu.unl.ec.gimnasia.domain.security.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "administrator")
@DiscriminatorValue("ADMINISTRATOR")
public class Administrator extends Person {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true)
    private User user;

    @OneToMany(mappedBy = "administrator", cascade = CascadeType.PERSIST)
    private final List<Competition> managedCompetitions = new ArrayList<>();

    @OneToMany(mappedBy = "registeringAdministrator", cascade = CascadeType.PERSIST)
    private final List<Gymnast> registeredGymnasts = new ArrayList<>();

    @ManyToMany
    @JoinTable(name = "administrator_judge",
            joinColumns = @JoinColumn(name = "administrator_id"),
            inverseJoinColumns = @JoinColumn(name = "judge_id"))
    private final List<Judge> registeredJudges = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "active_competition_id")
    private Competition activeCompetition;

    public Administrator() {
        super();
    }

    public Administrator(Long id, String nationalId, String firstName, String lastName, User user) {
        super(id, nationalId, firstName, lastName);
        this.user = user;
    }

    public void registerGymnast(Gymnast gymnast) {
        if (!registeredGymnasts.contains(gymnast)) {
            registeredGymnasts.add(gymnast);
        }
        gymnast.setRegisteringAdministrator(this);
    }

    public void registerJudge(Judge judge) {
        if (!registeredJudges.contains(judge)) {
            registeredJudges.add(judge);
        }
    }

    public void createCompetition(Competition competition) {
        if (!managedCompetitions.contains(competition)) {
            managedCompetitions.add(competition);
        }
        competition.setAdministrator(this);
    }

    public void selectActiveCompetition(Competition competition) {
        this.activeCompetition = competition;
    }

    public Competition getActiveCompetition() {
        return activeCompetition;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<Competition> getManagedCompetitions() {
        return managedCompetitions;
    }

    public List<Gymnast> getRegisteredGymnasts() {
        return registeredGymnasts;
    }

    public List<Judge> getRegisteredJudges() {
        return registeredJudges;
    }
}
