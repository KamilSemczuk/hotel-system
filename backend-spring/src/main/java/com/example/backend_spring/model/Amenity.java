package com.example.backend_spring.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "amenities")
@Getter
@Setter
@NoArgsConstructor
public class Amenity implements BaseEntity<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, unique = true)
    private String name;

    // Druga strona relacji wiele-do-wielu (właścicielem jest Room)
    @JsonIgnore
    @ManyToMany(mappedBy = "amenities")
    private Set<Room> rooms = new HashSet<>();

    public Amenity(String name) {
        this.name = name;
    }
}
