namespace HotelRevenue.Models;

// To, co Spring wysyła po utworzeniu rezerwacji
public record BookingRequest(
    long ReservationId,
    string RoomNumber,
    string Category,
    DateOnly CheckIn,
    DateOnly CheckOut,
    decimal TotalPrice);

public enum BookingStatus
{
    Active,
    Cancelled
}

public class Booking
{
    public long ReservationId { get; set; }
    public string RoomNumber { get; set; } = "";
    public string Category { get; set; } = "";
    public DateOnly CheckIn { get; set; }
    public DateOnly CheckOut { get; set; }
    public decimal TotalPrice { get; set; }
    public BookingStatus Status { get; set; } = BookingStatus.Active;
    public DateTime RegisteredAt { get; set; } = DateTime.Now;
    public DateTime? CancelledAt { get; set; }

    public int Nights => CheckOut.DayNumber - CheckIn.DayNumber;
    public decimal PricePerNight => Nights > 0 ? Math.Round(TotalPrice / Nights, 2) : 0;
}

public record DailyReport(
    DateOnly Date,
    int OccupiedRooms,
    int? TotalRooms,
    decimal? OccupancyPercent,
    decimal RoomRevenue,
    decimal Adr,
    decimal? RevPar,
    int NewBookings,
    int Cancellations);
