package edu.unl.ec.gimnasia.domain.competition;

import edu.unl.ec.gimnasia.domain.evaluation.Result;
import edu.unl.ec.gimnasia.domain.people.Administrator;
import edu.unl.ec.gimnasia.domain.people.Gymnast;
import edu.unl.ec.gimnasia.domain.people.Judge;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "competition")
@NamedQuery(name = "Competition.findAll", query = "SELECT c FROM Competition c ORDER BY c.date DESC")
public class Competition implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull @NotEmpty
    @Column(nullable = false, length = 150)
    private String name;

    @NotNull
    @Column(nullable = false)
    private LocalDate date;

    @NotNull @NotEmpty
    @Column(nullable = false, length = 150)
    private String location;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "administrator_id")
    private Administrator administrator;

    @Column(nullable = false)
    private boolean closed = false;

    @ManyToMany
    @JoinTable(name = "competition_judge",
            joinColumns = @JoinColumn(name = "competition_id"),
            inverseJoinColumns = @JoinColumn(name = "judge_id"))
    private final List<Judge> assignedJudges = new ArrayList<>();

    @OneToMany(mappedBy = "competition", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<Category> categories = new ArrayList<>();

    @ManyToMany
    @JoinTable(name = "competition_gymnast",
            joinColumns = @JoinColumn(name = "competition_id"),
            inverseJoinColumns = @JoinColumn(name = "gymnast_id"))
    private final List<Gymnast> participatingGymnasts = new ArrayList<>();

    @OneToMany( mappedBy = "competition", cascade = CascadeType.ALL, orphanRemoval = true )
    private final List<Result> results = new ArrayList<>();

    public Competition() {
    }

    public Competition(Long id, String name, LocalDate date, String location) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.location = location;
    }

    public void close() {
        this.closed = true;
    }

    public void reopen() {
        this.closed = false;
    }

    public void addJudge(Judge judge) {
        if (!assignedJudges.contains(judge)) {
            assignedJudges.add(judge);
        }
        judge.assignCompetition(this);
    }

    public void removeJudge(Judge judge) {
        assignedJudges.remove(judge);
        judge.unassignCompetition(this);
    }

    public void addCategory(Category category) {
        if (!categories.contains(category)) {
            categories.add(category);
        }
        category.setCompetition(this);
    }

    public boolean addGymnastToCategory(Gymnast gymnast, Category category) {
        boolean assigned = category.addGymnast(gymnast);
        if (assigned) {
            if (!participatingGymnasts.contains(gymnast)) {
                participatingGymnasts.add(gymnast);
            }
            gymnast.addCompetition(this);
        }
        return assigned;
    }

    public void addResult(Result result) {
        if (!results.contains(result)) {
            results.add(result);
        }
    }

    public void setAdministrator(Administrator administrator) {
        this.administrator = administrator;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getFormattedDate() {
        return date == null ? "" : date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Administrator getAdministrator() {
        return administrator;
    }

    public boolean isClosed() {
        return closed;
    }

    public List<Judge> getAssignedJudges() {
        return assignedJudges;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public List<Gymnast> getParticipatingGymnasts() {
        return participatingGymnasts;
    }

    public List<Result> getResults() {
        return results;
    }

    @Override
    public boolean equals( Object o ) {
        if (o == null || getClass() != o.getClass()) return false;
        Competition that = (Competition) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
