package com.example.backend_spring.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

// Odpowiedź z .NET: GET /pricing/quote
@JsonIgnoreProperties(ignoreUnknown = true)
public record PriceQuote(
        BigDecimal basePrice,
        LocalDate from,
        LocalDate to,
        int nights,
        BigDecimal total,
        List<NightPrice> breakdown
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record NightPrice(LocalDate date, BigDecimal price, List<String> modifiers) {
    }
}
