package com.example.backend_spring.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Map;

// Klient serwisu .NET. Gdy .NET nie działa, Spring działa dalej (cena bazowa, brak raportu finansowego).
@Component
public class RevenueClient {

    private static final Logger log = LoggerFactory.getLogger(RevenueClient.class);

    private final RestClient http;

    public RevenueClient(@Value("${revenue.api.url}") String baseUrl) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(2000);
        factory.setReadTimeout(5000);
        this.http = RestClient.builder()
                .baseUrl(baseUrl)
                .requestFactory(factory)
                .build();
    }

    // Cena za cały pobyt; przy braku .NET: cena bazowa × liczba nocy
    public BigDecimal quoteTotal(BigDecimal basePrice, LocalDate from, LocalDate to) {
        try {
            PriceQuote quote = http.get()
                    .uri(u -> u.path("/pricing/quote")
                            .queryParam("basePrice", basePrice)
                            .queryParam("from", from)
                            .queryParam("to", to)
                            .build())
                    .retrieve()
                    .body(PriceQuote.class);
            if (quote != null && quote.total() != null) {
                return quote.total();
            }
        } catch (Exception e) {
            log.warn("Serwis .NET niedostępny (pricing): {}", e.getMessage());
        }
        long nights = ChronoUnit.DAYS.between(from, to);
        return basePrice.multiply(BigDecimal.valueOf(nights));
    }

    public void registerBooking(Long reservationId, String roomNumber, String category,
                                LocalDate checkIn, LocalDate checkOut, BigDecimal totalPrice) {
        try {
            http.post()
                    .uri("/revenue/bookings")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of(
                            "reservationId", reservationId,
                            "roomNumber", roomNumber,
                            "category", category,
                            "checkIn", checkIn.toString(),
                            "checkOut", checkOut.toString(),
                            "totalPrice", totalPrice))
                    .retrieve()
                    .toBodilessEntity();
        } catch (Exception e) {
            log.warn("Nie udało się zarejestrować rezerwacji {} w .NET: {}", reservationId, e.getMessage());
        }
    }

    public void cancelBooking(Long reservationId) {
        try {
            http.patch()
                    .uri("/revenue/bookings/{id}/cancel", reservationId)
                    .retrieve()
                    .toBodilessEntity();
        } catch (Exception e) {
            log.warn("Nie udało się anulować rezerwacji {} w .NET: {}", reservationId, e.getMessage());
        }
    }

    // Surowy raport z .NET (obłożenie, przychód, ADR, RevPAR); null gdy .NET nie działa
    @SuppressWarnings("unchecked")
    public Map<String, Object> dailyReport(LocalDate date, long totalRooms) {
        try {
            return http.get()
                    .uri(u -> u.path("/revenue/reports/daily")
                            .queryParam("date", date)
                            .queryParam("totalRooms", totalRooms)
                            .build())
                    .retrieve()
                    .body(Map.class);
        } catch (Exception e) {
            log.warn("Serwis .NET niedostępny (raport): {}", e.getMessage());
            return null;
        }
    }
}
