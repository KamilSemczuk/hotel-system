package com.example.backend_spring.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Set;

public record RoomRequest(
        @NotBlank String number,
        int floor,
        @NotNull Long categoryId,
        Set<Long> amenityIds
) {
}
