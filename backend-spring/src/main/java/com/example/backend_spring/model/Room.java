package com.example.backend_spring.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "rooms")
@Getter
@Setter
@NoArgsConstructor
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String number;

    private int floor;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    // Relacja wiele-do-wielu: Hibernate tworzy tabelę łączącą room_amenity
    @ManyToMany
    @JoinTable(
            name = "room_amenity",
            joinColumns = @JoinColumn(name = "room_id"),
            inverseJoinColumns = @JoinColumn(name = "amenity_id")
    )
    private Set<Amenity> amenities = new HashSet<>();

    public Room(String number, int floor, Category category) {
        this.number = number;
        this.floor = floor;
        this.category = category;
    }

    public void addAmenity(Amenity amenity) {
        amenities.add(amenity);
        amenity.getRooms().add(this);
    }

    public void removeAmenity(Amenity amenity) {
        amenities.remove(amenity);
        amenity.getRooms().remove(this);
    }
}
