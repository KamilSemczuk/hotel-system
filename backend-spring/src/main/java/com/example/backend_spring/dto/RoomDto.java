package com.example.backend_spring.dto;

import com.example.backend_spring.model.Amenity;
import com.example.backend_spring.model.Room;

import java.math.BigDecimal;
import java.util.List;

public record RoomDto(
        Long id,
        String number,
        int floor,
        String category,
        int capacity,
        BigDecimal basePrice,
        List<String> amenities,
        BigDecimal totalPrice   // cena za pobyt (tylko w /available), wyliczona przez .NET
) {
    public static RoomDto from(Room room) {
        return from(room, null);
    }

    public static RoomDto from(Room room, BigDecimal totalPrice) {
        return new RoomDto(
                room.getId(),
                room.getNumber(),
                room.getFloor(),
                room.getCategory().getName(),
                room.getCategory().getCapacity(),
                room.getCategory().getBasePrice(),
                room.getAmenities().stream().map(Amenity::getName).sorted().toList(),
                totalPrice
        );
    }
}
