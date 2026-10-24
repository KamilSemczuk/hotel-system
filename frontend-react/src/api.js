// Wszystkie wywołania backendu (Spring Boot) w jednym miejscu.
// Adresy są względne (/api/...), więc działają i w dev (proxy Vite), i w Dockerze (proxy nginx).

async function request(path, options = {}) {
  const res = await fetch(`/api${path}`, {
    headers: { 'Content-Type': 'application/json' },
    ...options,
  })

  const text = await res.text()
  const data = text ? JSON.parse(text) : null

  if (!res.ok) {
    const error = new Error(data?.error ?? `Błąd serwera (${res.status})`)
    error.fields = data?.fields
    throw error
  }
  return data
}

export const api = {
  getRooms: () => request('/rooms'),
  getAvailableRooms: (from, to) => request(`/rooms/available?from=${from}&to=${to}`),

  getReservations: () => request('/reservations'),
  createReservation: (body) =>
    request('/reservations', { method: 'POST', body: JSON.stringify(body) }),
  cancelReservation: (id) => request(`/reservations/${id}/cancel`, { method: 'PATCH' }),
  checkIn: (id) => request(`/reservations/${id}/check-in`, { method: 'PATCH' }),
  checkOut: (id) => request(`/reservations/${id}/check-out`, { method: 'PATCH' }),

  getDailyReport: (date) => request(`/admin/reports/daily?date=${date}`),
}
