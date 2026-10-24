package com.example.backend_spring.controller;

import com.example.backend_spring.model.Amenity;
import com.example.backend_spring.service.AmenityService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/amenities")
public class AmenityController extends CrudController<Amenity, Long> {

    public AmenityController(AmenityService service) {
        super(service);
    }
}
