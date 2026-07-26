package edu.unl.ec.gimnasia.domain.evaluation;

import edu.unl.ec.gimnasia.domain.competition.Category;
import edu.unl.ec.gimnasia.domain.competition.Competition;
import edu.unl.ec.gimnasia.domain.competition.Specialty;
import edu.unl.ec.gimnasia.domain.people.Gymnast;
import edu.unl.ec.gimnasia.domain.people.Judge;
import edu.unl.ec.gimnasia.exception.InvalidScoreException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

@Entity
@Table(name = "evaluation")
@NamedQueries({
        @NamedQuery(name = "Evaluation.findExisting",
                query = "SELECT e FROM Evaluation e WHERE e.judge.id = :judgeId AND e.gymnast.id = :gymnastId "
                        + "AND e.competition.id = :competitionId AND e.criterion = :criterion"),
        @NamedQuery(name = "Evaluation.findByJudgeGymnastCompetition",
                query = "SELECT e FROM Evaluation e WHERE e.judge.id = :judgeId AND e.gymnast.id = :gymnastId "
                        + "AND e.competition.id = :competitionId"),
        @NamedQuery(name = "Evaluation.findByGymnastAndCompetition",
                query = "SELECT e FROM Evaluation e WHERE e.gymnast.id = :gymnastId AND e.competition.id = :competitionId"),
        @NamedQuery(name = "Evaluation.findByJudge", query = "SELECT e FROM Evaluation e WHERE e.judge.id = :judgeId")
})
public class Evaluation implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private double score;

    @Column(nullable = false)
    private LocalDate date;

    @Column(length = 100)
    private String criterion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "judge_id", nullable = false)
    private Judge judge;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gymnast_id", nullable = false)
    private Gymnast gymnast;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "competition_id", nullable = false)
    private Competition competition;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Specialty specialty;

    public Evaluation() {
    }

    public Evaluation(Long id, String criterion, double score, Judge judge, Gymnast gymnast,
                       Competition competition, Category category) throws InvalidScoreException {
        this.id = id;
        this.criterion = criterion;
        this.judge = judge;
        this.gymnast = gymnast;
        this.competition = competition;
        this.category = category;
        this.specialty = judge.getSpecialty();
        this.date = LocalDate.now();
        setScore(score);
        gymnast.addEvaluation(this);
        judge.issueEvaluation(this);
    }

    public void update(double newScore) throws InvalidScoreException {
        setScore(newScore);
        this.date = LocalDate.now();
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) throws InvalidScoreException {
        if (score < 0.0 || score > 1.0) {
            throw new InvalidScoreException("El valor debe estar entre 0.0 y 2.0");
        }
        this.score = score;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getFormattedDate() {
        return date == null ? "" : date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    public String getCriterion() {
        return criterion;
    }

    public void setCriterion(String criterion) {
        this.criterion = criterion;
    }

    public Judge getJudge() {
        return judge;
    }

    public Gymnast getGymnast() {
        return gymnast;
    }

    public Competition getCompetition() {
        return competition;
    }

    public Category getCategory() {
        return category;
    }

    public Specialty getSpecialty() {
        return specialty;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Evaluation that = (Evaluation) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
