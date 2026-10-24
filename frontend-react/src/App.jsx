import { useState } from 'react'
import GuestPage from './pages/GuestPage.jsx'
import AdminPage from './pages/AdminPage.jsx'

export default function App() {
  const [view, setView] = useState('guest')

  return (
    <div className="app">
      <header className="topbar">
        <div className="brand">
          <span className="brand-mark">H</span>
          <span>Hotel Panorama</span>
        </div>
        <nav className="nav">
          <button
            className={view === 'guest' ? 'nav-link active' : 'nav-link'}
            onClick={() => setView('guest')}
          >
            Rezerwacja
          </button>
          <button
            className={view === 'admin' ? 'nav-link active' : 'nav-link'}
            onClick={() => setView('admin')}
          >
            Panel recepcji
          </button>
        </nav>
      </header>

      <main className="content">{view === 'guest' ? <GuestPage /> : <AdminPage />}</main>
    </div>
  )
}
