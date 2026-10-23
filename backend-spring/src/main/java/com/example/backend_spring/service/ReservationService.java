package com.example.backend_spring.service;

import com.example.backend_spring.dto.CreateReservationRequest;
import com.example.backend_spring.dto.ReservationDto;
import com.example.backend_spring.exception.ConflictException;
import com.example.backend_spring.exception.NotFoundException;
import com.example.backend_spring.model.Guest;
import com.example.backend_spring.model.Reservation;
import com.example.backend_spring.model.ReservationStatus;
import com.example.backend_spring.model.Room;
import com.example.backend_spring.repository.GuestRepository;
import com.example.backend_spring.repository.ReservationRepository;
import com.example.backend_spring.repository.RoomRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@Transactional
public class ReservationService {

    private final ReservationRepository reservations;
    private final RoomRepository rooms;
    private final GuestRepository guests;

    public ReservationService(ReservationRepository reservations, RoomRepository rooms,
                              GuestRepository guests) {
        this.reservations = reservations;
        this.rooms = rooms;
        this.guests = guests;
    }

    @Transactional(readOnly = true)
    public List<ReservationDto> findAll() {
        return reservations.findAllByOrderByCheckInDesc().stream().map(ReservationDto::from).toList();
    }

    @Transactional(readOnly = true)
    public ReservationDto findById(Long id) {
        return ReservationDto.from(get(id));
    }

    public ReservationDto create(CreateReservationRequest req) {
        if (!req.checkOut().isAfter(req.checkIn())) {
            throw new IllegalArgumentException("Data wymeldowania musi być późniejsza niż data zameldowania");
        }

        Room room = rooms.findById(req.roomId())
                .orElseThrow(() -> new NotFoundException("Pokój " + req.roomId() + " nie istnieje"));

        if (reservations.existsOverlapping(room.getId(), req.checkIn(), req.checkOut(),
                ReservationStatus.CANCELLED)) {
            throw new ConflictException("Pokój " + room.getNumber() + " jest zajęty w tym terminie");
        }

        // Gość rozpoznawany po e-mailu; jeśli go nie ma, tworzymy nowego
        Guest guest = guests.findByEmail(req.email())
                .orElseGet(() -> guests.save(
                        new Guest(req.firstName(), req.lastName(), req.email(), req.phone())));

        Reservation reservation = new Reservation(room, guest, req.checkIn(), req.checkOut());

        // Na razie cena = cena bazowa × liczba nocy (później policzy ją serwis .NET)
        long nights = ChronoUnit.DAYS.between(req.checkIn(), req.checkOut());
        reservation.setTotalPrice(room.getCategory().getBasePrice().multiply(BigDecimal.valueOf(nights)));

        return ReservationDto.from(reservations.save(reservation));
    }

    public ReservationDto cancel(Long id) {
        Reservation r = get(id);
        requireStatus(r, ReservationStatus.BOOKED, "anulować");
        r.setStatus(ReservationStatus.CANCELLED);
        return ReservationDto.from(r);
    }

    public ReservationDto checkIn(Long id) {
        Reservation r = get(id);
        requireStatus(r, ReservationStatus.BOOKED, "zameldować");
        r.setStatus(ReservationStatus.CHECKED_IN);
        return ReservationDto.from(r);
    }

    public ReservationDto checkOut(Long id) {
        Reservation r = get(id);
        requireStatus(r, ReservationStatus.CHECKED_IN, "wymeldować");
        r.setStatus(ReservationStatus.CHECKED_OUT);
        return ReservationDto.from(r);
    }

    private Reservation get(Long id) {
        return reservations.findById(id)
                .orElseThrow(() -> new NotFoundException("Rezerwacja " + id + " nie istnieje"));
    }

    private void requireStatus(Reservation r, ReservationStatus expected, String action) {
        if (r.getStatus() != expected) {
            throw new ConflictException("Nie można " + action + " rezerwacji o statusie " + r.getStatus());
        }
    }
}
