package com.example.backend_spring.dto;

import com.example.backend_spring.model.Reservation;
import com.example.backend_spring.model.ReservationStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public record ReservationDto(
        Long id,
        Long roomId,
        String roomNumber,
        String category,
        Long guestId,
        String guestName,
        String guestEmail,
        LocalDate checkIn,
        LocalDate checkOut,
        long nights,
        ReservationStatus status,
        BigDecimal totalPrice,
        LocalDateTime createdAt
) {
    public static ReservationDto from(Reservation r) {
        return new ReservationDto(
                r.getId(),
                r.getRoom().getId(),
                r.getRoom().getNumber(),
                r.getRoom().getCategory().getName(),
                r.getGuest().getId(),
                r.getGuest().getFirstName() + " " + r.getGuest().getLastName(),
                r.getGuest().getEmail(),
                r.getCheckIn(),
                r.getCheckOut(),
                ChronoUnit.DAYS.between(r.getCheckIn(), r.getCheckOut()),
                r.getStatus(),
                r.getTotalPrice(),
                r.getCreatedAt()
        );
    }
}
