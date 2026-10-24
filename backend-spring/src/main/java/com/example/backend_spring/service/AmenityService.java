package com.example.backend_spring.service;

import com.example.backend_spring.model.Amenity;
import com.example.backend_spring.model.Room;
import com.example.backend_spring.repository.AmenityRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class AmenityService extends CrudService<Amenity, Long> {

    public AmenityService(AmenityRepository repository) {
        super(repository, "Udogodnienie");
    }

    // Przed usunięciem odpinamy udogodnienie od pokoi (czyścimy wiersze w room_amenity)
    @Override
    protected void beforeDelete(Amenity amenity) {
        for (Room room : new ArrayList<>(amenity.getRooms())) {
            room.removeAmenity(amenity);
        }
    }
}
