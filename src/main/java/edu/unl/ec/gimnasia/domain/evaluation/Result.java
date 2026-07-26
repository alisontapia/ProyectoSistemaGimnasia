package edu.unl.ec.gimnasia.domain.evaluation;

import edu.unl.ec.gimnasia.domain.competition.Category;
import edu.unl.ec.gimnasia.domain.competition.Competition;
import edu.unl.ec.gimnasia.domain.competition.Specialty;
import edu.unl.ec.gimnasia.domain.people.Gymnast;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Entity
@Table(name = "result")
@NamedQueries({
        @NamedQuery(name = "Result.findByGymnastAndCompetition",
                query = "SELECT r FROM Result r WHERE r.gymnast.id = :gymnastId AND r.competition.id = :competitionId"),
        @NamedQuery(name = "Result.findAllByCompetition",
                query = "SELECT r FROM Result r WHERE r.competition.id = :competitionId"),
        @NamedQuery(name = "Result.findAllByGymnast",
                query = "SELECT r FROM Result r WHERE r.gymnast.id = :gymnastId")
})
public class Result implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "final_score", nullable = false)
    private double finalScore;

    @Column(nullable = false)
    private int position;

    @Column(length = 30)
    private String medal;

    @Column(name = "generation_date", nullable = false)
    private LocalDate generationDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gymnast_id", nullable = false)
    private Gymnast gymnast;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "competition_id", nullable = false)
    private Competition competition;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @Transient
    private List<Evaluation> evaluations;

    public Result() {
    }

    public Result(Long id, Gymnast gymnast, Competition competition) {
        this.id = id;
        this.gymnast = gymnast;
        this.competition = competition;
        this.generationDate = LocalDate.now();
        this.position = 0;
        this.finalScore = 0.0;
    }

    public void recalculate(List<Evaluation> evaluations) {
        this.evaluations = evaluations;
        this.finalScore = totalsBySpecialty(evaluations).values().stream()
                .mapToDouble(Double::doubleValue)
                .sum();
        this.category = evaluations == null || evaluations.isEmpty() ? null : evaluations.get(0).getCategory();
    }

    public Map<Specialty, Double> totalsBySpecialty(List<Evaluation> evaluations) {
        Map<Specialty, Double> totals = new EnumMap<>(Specialty.class);
        for (Specialty specialty : Specialty.values()) {
            double sum = evaluations == null ? 0.0 : evaluations.stream()
                    .filter(e -> e.getSpecialty() == specialty)
                    .mapToDouble(Evaluation::getScore)
                    .sum();
            totals.put(specialty, specialty.calculateTotal(sum));
        }
        return totals;
    }

    public static boolean hasAllSpecialties(List<Evaluation> evaluations) {
        if (evaluations == null) {
            return false;
        }
        for (Specialty specialty : Specialty.values()) {
            boolean found = evaluations.stream().anyMatch(e -> e.getSpecialty() == specialty);
            if (!found) {
                return false;
            }
        }
        return true;
    }

    public void assignPosition(int position) {
        this.position = position;
        this.medal = switch (position) {
            case 1 -> "Medalla de Oro";
            case 2 -> "Medalla de Plata";
            case 3 -> "Medalla de Bronce";
            default -> "Sin medalla";
        };
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public double getFinalScore() {
        return finalScore;
    }

    public int getPosition() {
        return position;
    }

    public String getMedal() {
        return medal;
    }

    public LocalDate getGenerationDate() {
        return generationDate;
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

    public List<Evaluation> getEvaluations() {
        return evaluations;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Result result = (Result) o;
        return Objects.equals(id, result.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
