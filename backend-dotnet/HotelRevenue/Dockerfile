# Etap 1: budowanie i publikacja
FROM mcr.microsoft.com/dotnet/sdk:10.0 AS build
WORKDIR /src
COPY HotelRevenue.csproj .
RUN dotnet restore
COPY . .
RUN dotnet publish -c Release -o /app --no-restore

# Etap 2: środowisko uruchomieniowe ASP.NET
FROM mcr.microsoft.com/dotnet/aspnet:10.0
WORKDIR /app
COPY --from=build /app .
ENV ASPNETCORE_HTTP_PORTS=8080
EXPOSE 8080
ENTRYPOINT ["dotnet", "HotelRevenue.dll"]
