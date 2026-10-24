using HotelRevenue.Models;

namespace HotelRevenue.Services;

// Prosty "yield management": cena bazowa × mnożniki zależne od daty
public class PricingService
{
    private const decimal WeekendMultiplier = 1.20m; // noc z piątku i z soboty
    private const decimal SummerMultiplier = 1.30m;  // czerwiec–sierpień
    private const decimal HolidayMultiplier = 1.25m; // 20.12 – 2.01

    public PriceQuote Quote(decimal basePrice, DateOnly from, DateOnly to)
    {
        var breakdown = new List<NightPrice>();

        for (var date = from; date < to; date = date.AddDays(1))
        {
            var price = basePrice;
            var modifiers = new List<string>();

            if (date.DayOfWeek is DayOfWeek.Friday or DayOfWeek.Saturday)
            {
                price *= WeekendMultiplier;
                modifiers.Add("weekend +20%");
            }

            if (date.Month is >= 6 and <= 8)
            {
                price *= SummerMultiplier;
                modifiers.Add("sezon letni +30%");
            }

            if ((date.Month == 12 && date.Day >= 20) || (date.Month == 1 && date.Day <= 2))
            {
                price *= HolidayMultiplier;
                modifiers.Add("święta +25%");
            }

            breakdown.Add(new NightPrice(date, Math.Round(price, 2), modifiers));
        }

        var total = breakdown.Sum(n => n.Price);
        return new PriceQuote(basePrice, from, to, breakdown.Count, total, breakdown);
    }
}
