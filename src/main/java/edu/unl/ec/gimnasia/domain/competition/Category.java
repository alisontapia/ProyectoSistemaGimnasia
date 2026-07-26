package edu.unl.ec.gimnasia.domain.competition;

import edu.unl.ec.gimnasia.domain.people.Gymnast;
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
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "category")
public class Category implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull @NotEmpty
    @Column(nullable = false, length = 100)
    private String name;

    @NotNull @NotEmpty
    @Column(nullable = false, length = 50)
    private String level;

    @Column(name = "minimum_age", nullable = false)
    private int minimumAge;

    @Column(name = "maximum_age", nullable = false)
    private int maximumAge;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "competition_id", nullable = false)
    private Competition competition;

    @ManyToMany
    @JoinTable(name = "category_gymnast",
            joinColumns = @JoinColumn(name = "category_id"),
            inverseJoinColumns = @JoinColumn(name = "gymnast_id"))
    private final List<Gymnast> gymnasts = new ArrayList<>();

    public Category() {
    }

    public Category(Long id, String name, String level, int minimumAge, int maximumAge) {
        this.id = id;
        this.name = name;
        this.level = level;
        this.minimumAge = minimumAge;
        this.maximumAge = maximumAge;
    }

    public boolean isAgeInRange(Gymnast gymnast) {
        return gymnast.matchesAgeRange(minimumAge, maximumAge);
    }

    public boolean addGymnast(Gymnast gymnast) {
        if (!isAgeInRange(gymnast)) {
            return false;
        }
        if (!gymnasts.contains(gymnast)) {
            gymnasts.add(gymnast);
        }
        gymnast.addCategory(this);
        return true;
    }

    public void setCompetition(Competition competition) {
        this.competition = competition;
    }

    public Competition getCompetition() {
        return competition;
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

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public int getMinimumAge() {
        return minimumAge;
    }

    public void setMinimumAge(int minimumAge) {
        this.minimumAge = minimumAge;
    }

    public int getMaximumAge() {
        return maximumAge;
    }

    public void setMaximumAge(int maximumAge) {
        this.maximumAge = maximumAge;
    }

    public List<Gymnast> getGymnasts() {
        return gymnasts;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Category category = (Category) o;
        return Objects.equals(id, category.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
