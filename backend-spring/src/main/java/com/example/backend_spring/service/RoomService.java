package com.example.backend_spring.service;

import com.example.backend_spring.dto.RoomDto;
import com.example.backend_spring.model.ReservationStatus;
import com.example.backend_spring.repository.RoomRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class RoomService {

    private final RoomRepository roomRepository;

    public RoomService(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    public List<RoomDto> findAll() {
        return roomRepository.findAll().stream().map(RoomDto::from).toList();
    }

    public List<RoomDto> findAvailable(LocalDate from, LocalDate to) {
        if (!to.isAfter(from)) {
            throw new IllegalArgumentException("Data 'to' musi być późniejsza niż 'from'");
        }
        return roomRepository.findAvailable(from, to, ReservationStatus.CANCELLED)
                .stream().map(RoomDto::from).toList();
    }
}
