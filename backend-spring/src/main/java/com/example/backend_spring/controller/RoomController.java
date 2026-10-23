package com.example.backend_spring.controller;

import com.example.backend_spring.dto.RoomDto;
import com.example.backend_spring.service.RoomService;
import org.springframework.format.annotation.DateTimeFormat;
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
}
