package com.example.backend_spring.dto;

import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record CreateReservationRequest(
        @NotNull Long roomId,
        @NotBlank String firstName,
        @NotBlank String lastName,
        @NotBlank @Email String email,
        String phone,
        @NotNull @FutureOrPresent LocalDate checkIn,
        @NotNull @Future LocalDate checkOut
) {
}
