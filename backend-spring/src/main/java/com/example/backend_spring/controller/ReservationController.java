package com.example.backend_spring.controller;

import com.example.backend_spring.dto.CreateReservationRequest;
import com.example.backend_spring.dto.ReservationDto;
import com.example.backend_spring.service.ReservationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @GetMapping
    public List<ReservationDto> getAll() {
        return reservationService.findAll();
    }

    @GetMapping("/{id}")
    public ReservationDto getById(@PathVariable Long id) {
        return reservationService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReservationDto create(@Valid @RequestBody CreateReservationRequest request) {
        return reservationService.create(request);
    }

    @PatchMapping("/{id}/cancel")
    public ReservationDto cancel(@PathVariable Long id) {
        return reservationService.cancel(id);
    }

    @PatchMapping("/{id}/check-in")
    public ReservationDto checkIn(@PathVariable Long id) {
        return reservationService.checkIn(id);
    }

    @PatchMapping("/{id}/check-out")
    public ReservationDto checkOut(@PathVariable Long id) {
        return reservationService.checkOut(id);
    }
}
