package com.example.backend_spring.dto;

import java.time.LocalDate;
import java.util.Map;

// Raport łączący dane operacyjne (Spring) z finansowymi (.NET)
public record DailyReportDto(
        LocalDate date,
        long totalRooms,
        long arrivals,        // planowane zameldowania w tym dniu
        long departures,      // planowane wymeldowania w tym dniu
        long checkedInNow,    // goście aktualnie zameldowani
        Map<String, Object> financial  // z .NET: obłożenie, przychód, ADR, RevPAR (null gdy .NET nie działa)
) {
}
