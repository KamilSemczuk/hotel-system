import { useEffect, useState } from 'react'
import { api } from '../api.js'
import { formatDate, formatMoney, STATUS_LABELS, today } from '../utils.js'

const TABS = [
  { id: 'reservations', label: 'Rezerwacje' },
  { id: 'report', label: 'Raport dzienny' },
  { id: 'rooms', label: 'Pokoje' },
]

export default function AdminPage() {
  const [tab, setTab] = useState('reservations')

  return (
    <div className="page">
      <div className="tabs">
        {TABS.map((t) => (
          <button key={t.id} className={tab === t.id ? 'tab active' : 'tab'} onClick={() => setTab(t.id)}>
            {t.label}
          </button>
        ))}
      </div>
      {tab === 'reservations' && <Reservations />}
      {tab === 'report' && <DailyReport />}
      {tab === 'rooms' && <Rooms />}
    </div>
  )
}

function Reservations() {
  const [reservations, setReservations] = useState([])
  const [statusFilter, setStatusFilter] = useState('ALL')
  const [error, setError] = useState('')
  const [busyId, setBusyId] = useState(null)

  async function load() {
    try {
      setReservations(await api.getReservations())
      setError('')
    } catch (err) {
      setError(err.message)
    }
  }

  useEffect(() => {
    load()
  }, [])

  async function runAction(id, action) {
    setBusyId(id)
    try {
      await action(id)
      await load()
    } catch (err) {
      setError(err.message)
    } finally {
      setBusyId(null)
    }
  }

  const visible = statusFilter === 'ALL'
    ? reservations
    : reservations.filter((r) => r.status === statusFilter)

  return (
    <section className="card">
      <div className="card-head">
        <h2>Rezerwacje</h2>
        <div className="row">
          <select value={statusFilter} onChange={(e) => setStatusFilter(e.target.value)}>
            <option value="ALL">Wszystkie statusy</option>
            {Object.entries(STATUS_LABELS).map(([value, label]) => (
              <option key={value} value={value}>{label}</option>
            ))}
          </select>
          <button className="btn" onClick={load}>Odśwież</button>
        </div>
      </div>

      {error && <div className="alert error">{error}</div>}

      <div className="table-wrap">
        <table>
          <thead>
            <tr>
              <th>#</th><th>Pokój</th><th>Gość</th><th>Termin</th><th>Status</th><th className="num">Cena</th><th></th>
            </tr>
          </thead>
          <tbody>
            {visible.map((r) => (
              <tr key={r.id}>
                <td>{r.id}</td>
                <td>{r.roomNumber} <span className="muted small">{r.category}</span></td>
                <td>{r.guestName}<div className="muted small">{r.guestEmail}</div></td>
                <td>{formatDate(r.checkIn)} – {formatDate(r.checkOut)}<div className="muted small">{r.nights} noce</div></td>
                <td><span className={`badge ${r.status.toLowerCase()}`}>{STATUS_LABELS[r.status]}</span></td>
                <td className="num">{formatMoney(r.totalPrice)}</td>
                <td className="actions">
                  {r.status === 'BOOKED' && (
                    <>
                      <button className="btn small primary" disabled={busyId === r.id} onClick={() => runAction(r.id, api.checkIn)}>Zamelduj</button>
                      <button className="btn small danger" disabled={busyId === r.id} onClick={() => runAction(r.id, api.cancelReservation)}>Anuluj</button>
                    </>
                  )}
                  {r.status === 'CHECKED_IN' && (
                    <button className="btn small primary" disabled={busyId === r.id} onClick={() => runAction(r.id, api.checkOut)}>Wymelduj</button>
                  )}
                </td>
              </tr>
            ))}
            {visible.length === 0 && (
              <tr><td colSpan="7" className="muted center">Brak rezerwacji</td></tr>
            )}
          </tbody>
        </table>
      </div>
    </section>
  )
}

function DailyReport() {
  const [date, setDate] = useState(today())
  const [report, setReport] = useState(null)
  const [error, setError] = useState('')

  useEffect(() => {
    api.getDailyReport(date)
      .then((data) => { setReport(data); setError('') })
      .catch((err) => setError(err.message))
  }, [date])

  const fin = report?.financial

  return (
    <section className="card">
      <div className="card-head">
        <h2>Raport dzienny</h2>
        <input type="date" value={date} onChange={(e) => setDate(e.target.value)} />
      </div>

      {error && <div className="alert error">{error}</div>}

      {report && (
        <>
          <h3 className="sub">Operacyjnie (Spring)</h3>
          <div className="stats">
            <Stat label="Pokoje w hotelu" value={report.totalRooms} />
            <Stat label="Przyjazdy" value={report.arrivals} />
            <Stat label="Wyjazdy" value={report.departures} />
            <Stat label="Zameldowani teraz" value={report.checkedInNow} />
          </div>

          <h3 className="sub">Finansowo (.NET)</h3>
          {fin ? (
            <div className="stats">
              <Stat label="Obłożenie" value={fin.occupancyPercent != null ? `${fin.occupancyPercent}%` : '–'} hint={`${fin.occupiedRooms} z ${report.totalRooms} pokoi`} />
              <Stat label="Przychód z pokoi" value={formatMoney(fin.roomRevenue)} />
              <Stat label="ADR" value={formatMoney(fin.adr)} hint="średnia cena za zajęty pokój" />
              <Stat label="RevPAR" value={formatMoney(fin.revPar)} hint="przychód na dostępny pokój" />
            </div>
          ) : (
            <div className="alert">Serwis .NET jest niedostępny – brak danych finansowych.</div>
          )}
        </>
      )}
    </section>
  )
}

function Stat({ label, value, hint }) {
  return (
    <div className="stat">
      <span className="stat-label">{label}</span>
      <span className="stat-value">{value}</span>
      {hint && <span className="muted small">{hint}</span>}
    </div>
  )
}

function Rooms() {
  const [rooms, setRooms] = useState([])
  const [error, setError] = useState('')

  useEffect(() => {
    api.getRooms().then(setRooms).catch((err) => setError(err.message))
  }, [])

  return (
    <section className="card">
      <div className="card-head"><h2>Pokoje</h2></div>
      {error && <div className="alert error">{error}</div>}
      <div className="table-wrap">
        <table>
          <thead>
            <tr><th>Numer</th><th>Kategoria</th><th>Piętro</th><th>Osoby</th><th>Udogodnienia</th><th className="num">Cena bazowa</th></tr>
          </thead>
          <tbody>
            {rooms.map((r) => (
              <tr key={r.id}>
                <td>{r.number}</td>
                <td>{r.category}</td>
                <td>{r.floor}</td>
                <td>{r.capacity}</td>
                <td><ul className="chips">{r.amenities.map((a) => <li key={a}>{a}</li>)}</ul></td>
                <td className="num">{formatMoney(r.basePrice)}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </section>
  )
}
