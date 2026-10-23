package com.example.backend_spring.repository;

import com.example.backend_spring.model.Reservation;
import com.example.backend_spring.model.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findByStatus(ReservationStatus status);

    List<Reservation> findAllByOrderByCheckInDesc();

    // Czy pokój ma aktywną rezerwację nachodzącą na termin [from, to)
    @Query("""
            select count(res) > 0 from Reservation res
            where res.room.id = :roomId
              and res.status <> :cancelled
              and res.checkIn < :to
              and res.checkOut > :from
            """)
    boolean existsOverlapping(@Param("roomId") Long roomId,
                              @Param("from") LocalDate from,
                              @Param("to") LocalDate to,
                              @Param("cancelled") ReservationStatus cancelled);
}
