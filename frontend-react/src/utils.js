// Data w formacie RRRR-MM-DD w lokalnej strefie czasowej
export function toIsoDate(date) {
  const y = date.getFullYear()
  const m = String(date.getMonth() + 1).padStart(2, '0')
  const d = String(date.getDate()).padStart(2, '0')
  return `${y}-${m}-${d}`
}

export function addDays(isoDate, days) {
  const date = new Date(`${isoDate}T00:00:00`)
  date.setDate(date.getDate() + days)
  return toIsoDate(date)
}

export function today() {
  return toIsoDate(new Date())
}

export function nightsBetween(from, to) {
  const ms = new Date(`${to}T00:00:00`) - new Date(`${from}T00:00:00`)
  return Math.round(ms / 86_400_000)
}

const money = new Intl.NumberFormat('pl-PL', { style: 'currency', currency: 'PLN' })
export function formatMoney(value) {
  return value == null ? '–' : money.format(value)
}

export function formatDate(isoDate) {
  return new Date(`${isoDate}T00:00:00`).toLocaleDateString('pl-PL', {
    day: 'numeric',
    month: 'short',
    year: 'numeric',
  })
}

export const STATUS_LABELS = {
  BOOKED: 'Zarezerwowana',
  CHECKED_IN: 'Zameldowany',
  CHECKED_OUT: 'Wymeldowany',
  CANCELLED: 'Anulowana',
}
