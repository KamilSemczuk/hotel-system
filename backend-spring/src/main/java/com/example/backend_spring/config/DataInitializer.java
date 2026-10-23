package com.example.backend_spring.config;

import com.example.backend_spring.model.*;
import com.example.backend_spring.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

// Wstawia przykładowe dane przy pierwszym starcie (gdy baza jest pusta)
@Component
public class DataInitializer implements CommandLineRunner {

    private final CategoryRepository categories;
    private final AmenityRepository amenities;
    private final RoomRepository rooms;
    private final GuestRepository guests;
    private final ReservationRepository reservations;

    public DataInitializer(CategoryRepository categories, AmenityRepository amenities,
                           RoomRepository rooms, GuestRepository guests,
                           ReservationRepository reservations) {
        this.categories = categories;
        this.amenities = amenities;
        this.rooms = rooms;
        this.guests = guests;
        this.reservations = reservations;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (categories.count() > 0) {
            return;
        }

        Category single = categories.save(new Category("Single", "Pokój jednoosobowy", 1, new BigDecimal("200.00")));
        Category dbl = categories.save(new Category("Double", "Pokój dwuosobowy", 2, new BigDecimal("320.00")));
        Category suite = categories.save(new Category("Suite", "Apartament", 4, new BigDecimal("650.00")));

        Amenity wifi = amenities.save(new Amenity("Wi-Fi"));
        Amenity tv = amenities.save(new Amenity("TV"));
        Amenity ac = amenities.save(new Amenity("Klimatyzacja"));
        Amenity minibar = amenities.save(new Amenity("Minibar"));
        Amenity balcony = amenities.save(new Amenity("Balkon"));

        Room r101 = new Room("101", 1, single);
        r101.addAmenity(wifi);
        r101.addAmenity(tv);

        Room r102 = new Room("102", 1, single);
        r102.addAmenity(wifi);

        Room r201 = new Room("201", 2, dbl);
        r201.addAmenity(wifi);
        r201.addAmenity(tv);
        r201.addAmenity(ac);

        Room r202 = new Room("202", 2, dbl);
        r202.addAmenity(wifi);
        r202.addAmenity(tv);
        r202.addAmenity(balcony);

        Room r301 = new Room("301", 3, suite);
        r301.addAmenity(wifi);
        r301.addAmenity(tv);
        r301.addAmenity(ac);
        r301.addAmenity(minibar);
        r301.addAmenity(balcony);

        rooms.save(r101);
        rooms.save(r102);
        rooms.save(r201);
        rooms.save(r202);
        rooms.save(r301);

        Guest jan = guests.save(new Guest("Jan", "Kowalski", "jan.kowalski@example.com", "500100200"));
        LocalDate today = LocalDate.now();
        Reservation res = new Reservation(r201, jan, today.plusDays(1), today.plusDays(4));
        res.setTotalPrice(new BigDecimal("960.00"));
        reservations.save(res);
    }
}
