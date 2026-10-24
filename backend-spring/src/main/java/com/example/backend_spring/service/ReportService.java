package com.example.backend_spring.service;

import com.example.backend_spring.client.RevenueClient;
import com.example.backend_spring.dto.DailyReportDto;
import com.example.backend_spring.model.ReservationStatus;
import com.example.backend_spring.repository.ReservationRepository;
import com.example.backend_spring.repository.RoomRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@Transactional(readOnly = true)
public class ReportService {

    private final RoomRepository rooms;
    private final ReservationRepository reservations;
    private final RevenueClient revenueClient;

    public ReportService(RoomRepository rooms, ReservationRepository reservations,
                         RevenueClient revenueClient) {
        this.rooms = rooms;
        this.reservations = reservations;
        this.revenueClient = revenueClient;
    }

    public DailyReportDto daily(LocalDate date) {
        long totalRooms = rooms.count();
        return new DailyReportDto(
                date,
                totalRooms,
                reservations.countByCheckInAndStatusNot(date, ReservationStatus.CANCELLED),
                reservations.countByCheckOutAndStatusNot(date, ReservationStatus.CANCELLED),
                reservations.countByStatus(ReservationStatus.CHECKED_IN),
                revenueClient.dailyReport(date, totalRooms)
        );
    }
}
