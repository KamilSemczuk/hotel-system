using HotelRevenue.Models;
using HotelRevenue.Services;
using System.Text.Json.Serialization;
using Microsoft.AspNetCore.Mvc;

var builder = WebApplication.CreateBuilder(args);

builder.Services.AddSingleton<PricingService>();
builder.Services.AddSingleton<RevenueStore>();
builder.Services.ConfigureHttpJsonOptions(o =>
    o.SerializerOptions.Converters.Add(new JsonStringEnumConverter()));

var app = builder.Build();

app.MapGet("/", () => "HotelRevenue API działa");

// ---------- Ceny dynamiczne ----------
// GET /pricing/quote?basePrice=320&from=2026-11-06&to=2026-11-09
app.MapGet("/pricing/quote", (
    [FromQuery] decimal basePrice,
    [FromQuery(Name = "from")] DateOnly fromDate,
    [FromQuery(Name = "to")] DateOnly toDate,
    PricingService pricing) =>
{
    if (basePrice <= 0)
        return Results.BadRequest(new { error = "basePrice musi być większe od 0" });
    if (toDate <= fromDate)
        return Results.BadRequest(new { error = "Data 'to' musi być późniejsza niż 'from'" });

    return Results.Ok(pricing.Quote(basePrice, fromDate, toDate));
});

// ---------- Przychody ----------
app.MapPost("/revenue/bookings", (BookingRequest req, RevenueStore store) =>
{
    if (req.CheckOut <= req.CheckIn)
        return Results.BadRequest(new { error = "CheckOut musi być późniejszy niż CheckIn" });

    var booking = store.Register(req);
    return Results.Created($"/revenue/bookings/{booking.ReservationId}", booking);
});

app.MapPatch("/revenue/bookings/{reservationId:long}/cancel", (long reservationId, RevenueStore store) =>
{
    var booking = store.Cancel(reservationId);
    return booking is null
        ? Results.NotFound(new { error = $"Rezerwacja {reservationId} nie jest zarejestrowana" })
        : Results.Ok(booking);
});

app.MapGet("/revenue/bookings", (RevenueStore store) => store.All());

// GET /revenue/reports/daily?date=2026-11-10&totalRooms=5
app.MapGet("/revenue/reports/daily", (
    [FromQuery] DateOnly? date,
    [FromQuery] int? totalRooms,
    RevenueStore store) =>
{
    var day = date ?? DateOnly.FromDateTime(DateTime.Today);
    return Results.Ok(store.Daily(day, totalRooms));
});

app.Run();
