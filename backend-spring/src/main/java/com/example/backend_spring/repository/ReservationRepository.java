package com.example.backend_spring.repository;

import com.example.backend_spring.model.Reservation;
import com.example.backend_spring.model.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByStatus(ReservationStatus status);
}
