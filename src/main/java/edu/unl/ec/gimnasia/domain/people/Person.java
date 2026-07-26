package edu.unl.ec.gimnasia.domain.people;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "person")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "person_type", discriminatorType = jakarta.persistence.DiscriminatorType.STRING)
public abstract class Person implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull @NotEmpty
    @Column(name = "national_id", nullable = false, unique = true, length = 20)
    private String nationalId;

    @NotNull @NotEmpty
    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @NotNull @NotEmpty
    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    protected Person() {
    }

    protected Person(Long id, @NotNull @NotEmpty String nationalId,
                      @NotNull @NotEmpty String firstName, @NotNull @NotEmpty String lastName) {
        this.id = id;
        setNationalId(nationalId);
        setFirstName(firstName);
        setLastName(lastName);
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNationalId() {
        return nationalId;
    }

    public void setNationalId(@NotNull @NotEmpty String nationalId) {
        this.nationalId = nationalId.trim();
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(@NotNull @NotEmpty String firstName) {
        this.firstName = firstName.trim();
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(@NotNull @NotEmpty String lastName) {
        this.lastName = lastName.trim();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Person person = (Person) o;
        return Objects.equals(id, person.id) && Objects.equals(nationalId, person.nationalId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nationalId);
    }
}
