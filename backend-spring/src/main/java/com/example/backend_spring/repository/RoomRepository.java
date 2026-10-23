package com.example.backend_spring.repository;

import com.example.backend_spring.model.ReservationStatus;
import com.example.backend_spring.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface RoomRepository extends JpaRepository<Room, Long> {

    // Pokoje bez rezerwacji nachodzącej na termin [from, to)
    @Query("""
            select r from Room r
            where r.id not in (
                select res.room.id from Reservation res
                where res.status <> :cancelled
                  and res.checkIn < :to
                  and res.checkOut > :from
            )
            order by r.number
            """)
    List<Room> findAvailable(@Param("from") LocalDate from,
                             @Param("to") LocalDate to,
                             @Param("cancelled") ReservationStatus cancelled);
}
