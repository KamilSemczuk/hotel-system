import { useState } from 'react'
import { api } from '../api.js'
import { addDays, formatDate, formatMoney, nightsBetween, today } from '../utils.js'

const EMPTY_GUEST = { firstName: '', lastName: '', email: '', phone: '' }

export default function GuestPage() {
  const [from, setFrom] = useState(addDays(today(), 1))
  const [to, setTo] = useState(addDays(today(), 3))
  const [rooms, setRooms] = useState(null)
  const [searched, setSearched] = useState(null) // { from, to } z ostatniego wyszukiwania
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')

  const [selectedRoom, setSelectedRoom] = useState(null)
  const [guest, setGuest] = useState(EMPTY_GUEST)
  const [fieldErrors, setFieldErrors] = useState({})
  const [saving, setSaving] = useState(false)
  const [confirmation, setConfirmation] = useState(null)

  async function search(e) {
    e.preventDefault()
    setError('')
    setSelectedRoom(null)
    setConfirmation(null)
    setLoading(true)
    try {
      setRooms(await api.getAvailableRooms(from, to))
      setSearched({ from, to })
    } catch (err) {
      setError(err.message)
      setRooms(null)
    } finally {
      setLoading(false)
    }
  }

  async function reserve(e) {
    e.preventDefault()
    setError('')
    setFieldErrors({})
    setSaving(true)
    try {
      const reservation = await api.createReservation({
        roomId: selectedRoom.id,
        ...guest,
        checkIn: searched.from,
        checkOut: searched.to,
      })
      setConfirmation(reservation)
      setSelectedRoom(null)
      setGuest(EMPTY_GUEST)
      setRooms(null)
    } catch (err) {
      setError(err.message)
      setFieldErrors(err.fields ?? {})
    } finally {
      setSaving(false)
    }
  }

  function updateGuest(field, value) {
    setGuest((g) => ({ ...g, [field]: value }))
  }

  return (
    <div className="page">
      <section className="hero">
        <h1>Znajdź pokój na swój termin</h1>
        <p>Ceny liczone dynamicznie: weekendy, sezon letni i święta są droższe.</p>

        <form className="search" onSubmit={search}>
          <label>
            Przyjazd
            <input type="date" value={from} min={today()} onChange={(e) => setFrom(e.target.value)} required />
          </label>
          <label>
            Wyjazd
            <input type="date" value={to} min={addDays(from, 1)} onChange={(e) => setTo(e.target.value)} required />
          </label>
          <button className="btn primary" disabled={loading}>
            {loading ? 'Szukam…' : 'Szukaj'}
          </button>
        </form>
      </section>

      {error && <div className="alert error">{error}</div>}

      {confirmation && (
        <section className="card confirmation">
          <h2>Rezerwacja potwierdzona</h2>
          <p className="muted">Numer rezerwacji: <strong>#{confirmation.id}</strong></p>
          <dl className="summary">
            <dt>Gość</dt><dd>{confirmation.guestName}</dd>
            <dt>Pokój</dt><dd>{confirmation.roomNumber} ({confirmation.category})</dd>
            <dt>Termin</dt>
            <dd>{formatDate(confirmation.checkIn)} – {formatDate(confirmation.checkOut)} ({confirmation.nights} noce)</dd>
            <dt>Do zapłaty</dt><dd className="price">{formatMoney(confirmation.totalPrice)}</dd>
          </dl>
          <button className="btn" onClick={() => setConfirmation(null)}>Zarezerwuj kolejny pokój</button>
        </section>
      )}

      {rooms && (
        <section>
          <h2 className="section-title">
            Wolne pokoje: {rooms.length}
            <span className="muted"> · {formatDate(searched.from)} – {formatDate(searched.to)}, {nightsBetween(searched.from, searched.to)} noce</span>
          </h2>

          {rooms.length === 0 && <p className="muted">Brak wolnych pokoi w tym terminie.</p>}

          <div className="room-grid">
            {rooms.map((room) => (
              <article key={room.id} className={selectedRoom?.id === room.id ? 'card room selected' : 'card room'}>
                <div className="room-head">
                  <div>
                    <h3>Pokój {room.number}</h3>
                    <p className="muted">{room.category} · do {room.capacity} os. · piętro {room.floor}</p>
                  </div>
                  <div className="room-price">
                    <span className="price">{formatMoney(room.totalPrice)}</span>
                    <span className="muted small">od {formatMoney(room.basePrice)} / noc</span>
                  </div>
                </div>
                <ul className="chips">
                  {room.amenities.map((a) => <li key={a}>{a}</li>)}
                </ul>
                <button className="btn primary" onClick={() => { setSelectedRoom(room); setFieldErrors({}) }}>
                  Rezerwuj
                </button>
              </article>
            ))}
          </div>
        </section>
      )}

      {selectedRoom && (
        <section className="card">
          <h2>Dane gościa – pokój {selectedRoom.number}</h2>
          <p className="muted">
            {formatDate(searched.from)} – {formatDate(searched.to)} · {formatMoney(selectedRoom.totalPrice)}
          </p>
          <form className="guest-form" onSubmit={reserve}>
            <Field label="Imię" error={fieldErrors.firstName}>
              <input value={guest.firstName} onChange={(e) => updateGuest('firstName', e.target.value)} required />
            </Field>
            <Field label="Nazwisko" error={fieldErrors.lastName}>
              <input value={guest.lastName} onChange={(e) => updateGuest('lastName', e.target.value)} required />
            </Field>
            <Field label="E-mail" error={fieldErrors.email}>
              <input type="email" value={guest.email} onChange={(e) => updateGuest('email', e.target.value)} required />
            </Field>
            <Field label="Telefon" error={fieldErrors.phone}>
              <input value={guest.phone} onChange={(e) => updateGuest('phone', e.target.value)} />
            </Field>
            <div className="form-actions">
              <button type="button" className="btn" onClick={() => setSelectedRoom(null)}>Anuluj</button>
              <button className="btn primary" disabled={saving}>{saving ? 'Zapisuję…' : 'Potwierdź rezerwację'}</button>
            </div>
          </form>
        </section>
      )}
    </div>
  )
}

function Field({ label, error, children }) {
  return (
    <label className="field">
      {label}
      {children}
      {error && <span className="field-error">{error}</span>}
    </label>
  )
}
