using System.Collections.Concurrent;
using HotelRevenue.Models;

namespace HotelRevenue.Services;

// Dane trzymane w pamięci (znikają po restarcie serwisu) – wystarczy na MVP
public class RevenueStore
{
    private readonly ConcurrentDictionary<long, Booking> _bookings = new();

    public Booking Register(BookingRequest req)
    {
        var booking = new Booking
        {
            ReservationId = req.ReservationId,
            RoomNumber = req.RoomNumber,
            Category = req.Category,
            CheckIn = req.CheckIn,
            CheckOut = req.CheckOut,
            TotalPrice = req.TotalPrice
        };
        _bookings[req.ReservationId] = booking;
        return booking;
    }

    public Booking? Cancel(long reservationId)
    {
        if (!_bookings.TryGetValue(reservationId, out var booking))
        {
            return null;
        }
        booking.Status = BookingStatus.Cancelled;
        booking.CancelledAt = DateTime.Now;
        return booking;
    }

    public IEnumerable<Booking> All() => _bookings.Values.OrderBy(b => b.ReservationId);

    public DailyReport Daily(DateOnly date, int? totalRooms)
    {
        var active = _bookings.Values.Where(b => b.Status == BookingStatus.Active).ToList();

        // Pokoje zajęte w nocy z "date" na "date + 1"
        var staying = active.Where(b => b.CheckIn <= date && date < b.CheckOut).ToList();

        var occupied = staying.Count;
        var revenue = staying.Sum(b => b.PricePerNight);
        var adr = occupied > 0 ? Math.Round(revenue / occupied, 2) : 0;

        decimal? occupancy = null;
        decimal? revPar = null;
        if (totalRooms is > 0)
        {
            occupancy = Math.Round(100m * occupied / totalRooms.Value, 1);
            revPar = Math.Round(revenue / totalRooms.Value, 2);
        }

        var newBookings = _bookings.Values.Count(b => DateOnly.FromDateTime(b.RegisteredAt) == date);
        var cancellations = _bookings.Values.Count(b =>
            b.CancelledAt is not null && DateOnly.FromDateTime(b.CancelledAt.Value) == date);

        return new DailyReport(date, occupied, totalRooms, occupancy, revenue, adr, revPar,
            newBookings, cancellations);
    }
}
