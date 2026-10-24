package com.example.backend_spring.controller;

import com.example.backend_spring.dto.RoomDto;
import com.example.backend_spring.dto.RoomRequest;
import com.example.backend_spring.service.RoomService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/rooms")
public class RoomController {

    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @GetMapping
    public List<RoomDto> getAll() {
        return roomService.findAll();
    }

    // np. GET /api/rooms/available?from=2026-11-01&to=2026-11-05
    @GetMapping("/available")
    public List<RoomDto> getAvailable(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return roomService.findAvailable(from, to);
    }

    @GetMapping("/{id}")
    public RoomDto getById(@PathVariable Long id) {
        return roomService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RoomDto create(@Valid @RequestBody RoomRequest request) {
        return roomService.create(request);
    }

    @PutMapping("/{id}")
    public RoomDto update(@PathVariable Long id, @Valid @RequestBody RoomRequest request) {
        return roomService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        roomService.delete(id);
    }

    // Relacja M:N – przypięcie / odpięcie udogodnienia
    @PutMapping("/{roomId}/amenities/{amenityId}")
    public RoomDto addAmenity(@PathVariable Long roomId, @PathVariable Long amenityId) {
        return roomService.addAmenity(roomId, amenityId);
    }

    @DeleteMapping("/{roomId}/amenities/{amenityId}")
    public RoomDto removeAmenity(@PathVariable Long roomId, @PathVariable Long amenityId) {
        return roomService.removeAmenity(roomId, amenityId);
    }
}
