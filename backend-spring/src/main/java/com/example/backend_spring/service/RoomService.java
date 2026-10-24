package com.example.backend_spring.service;

import com.example.backend_spring.client.RevenueClient;
import com.example.backend_spring.dto.RoomDto;
import com.example.backend_spring.dto.RoomRequest;
import com.example.backend_spring.exception.NotFoundException;
import com.example.backend_spring.model.Amenity;
import com.example.backend_spring.model.Category;
import com.example.backend_spring.model.ReservationStatus;
import com.example.backend_spring.model.Room;
import com.example.backend_spring.repository.AmenityRepository;
import com.example.backend_spring.repository.CategoryRepository;
import com.example.backend_spring.repository.RoomRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class RoomService {

    private final RoomRepository roomRepository;
    private final CategoryRepository categoryRepository;
    private final AmenityRepository amenityRepository;
    private final RevenueClient revenueClient;

    public RoomService(RoomRepository roomRepository, CategoryRepository categoryRepository,
                       AmenityRepository amenityRepository, RevenueClient revenueClient) {
        this.roomRepository = roomRepository;
        this.categoryRepository = categoryRepository;
        this.amenityRepository = amenityRepository;
        this.revenueClient = revenueClient;
    }

    public List<RoomDto> findAll() {
        return roomRepository.findAll().stream().map(RoomDto::from).toList();
    }

    public RoomDto findById(Long id) {
        return RoomDto.from(getRoom(id));
    }

    // Wolne pokoje + aktualna cena za pobyt z serwisu .NET
    public List<RoomDto> findAvailable(LocalDate from, LocalDate to) {
        if (!to.isAfter(from)) {
            throw new IllegalArgumentException("Data 'to' musi być późniejsza niż 'from'");
        }
        return roomRepository.findAvailable(from, to, ReservationStatus.CANCELLED).stream()
                .map(room -> RoomDto.from(room,
                        revenueClient.quoteTotal(room.getCategory().getBasePrice(), from, to)))
                .toList();
    }

    @Transactional
    public RoomDto create(RoomRequest req) {
        Room room = new Room(req.number(), req.floor(), getCategory(req.categoryId()));
        setAmenities(room, req);
        return RoomDto.from(roomRepository.save(room));
    }

    @Transactional
    public RoomDto update(Long id, RoomRequest req) {
        Room room = getRoom(id);
        room.setNumber(req.number());
        room.setFloor(req.floor());
        room.setCategory(getCategory(req.categoryId()));
        setAmenities(room, req);
        return RoomDto.from(room);
    }

    @Transactional
    public void delete(Long id) {
        roomRepository.delete(getRoom(id));
        roomRepository.flush();
    }

    // Relacja wiele-do-wielu: dodanie / usunięcie udogodnienia z pokoju
    @Transactional
    public RoomDto addAmenity(Long roomId, Long amenityId) {
        Room room = getRoom(roomId);
        room.addAmenity(getAmenity(amenityId));
        return RoomDto.from(room);
    }

    @Transactional
    public RoomDto removeAmenity(Long roomId, Long amenityId) {
        Room room = getRoom(roomId);
        room.removeAmenity(getAmenity(amenityId));
        return RoomDto.from(room);
    }

    private void setAmenities(Room room, RoomRequest req) {
        for (Amenity a : new ArrayList<>(room.getAmenities())) {
            room.removeAmenity(a);
        }
        if (req.amenityIds() != null) {
            req.amenityIds().forEach(id -> room.addAmenity(getAmenity(id)));
        }
    }

    private Room getRoom(Long id) {
        return roomRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Pokój o id " + id + " nie istnieje"));
    }

    private Category getCategory(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Kategoria o id " + id + " nie istnieje"));
    }

    private Amenity getAmenity(Long id) {
        return amenityRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Udogodnienie o id " + id + " nie istnieje"));
    }
}
