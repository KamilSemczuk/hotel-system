namespace HotelRevenue.Models;

public record NightPrice(DateOnly Date, decimal Price, List<string> Modifiers);

public record PriceQuote(
    decimal BasePrice,
    DateOnly From,
    DateOnly To,
    int Nights,
    decimal Total,
    List<NightPrice> Breakdown);
