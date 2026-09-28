import { useEffect, useState } from 'react';
import { NavLink, Outlet, useLocation } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { useI18n } from '../context/I18nContext';

export default function AppShell() {
  const [open, setOpen] = useState(false);
  const [dark, setDark] = useState(() => localStorage.getItem('khata_theme') === 'dark');
  const { user, logout } = useAuth();
  const { t } = useI18n();
  const location = useLocation();
  useEffect(() => { document.documentElement.dataset.theme = dark ? 'dark' : 'light'; localStorage.setItem('khata_theme', dark ? 'dark' : 'light'); }, [dark]);
  const pageTitle = location.pathname === '/transactions' ? t('transactions') : location.pathname === '/settings' ? t('settings') : location.pathname.startsWith('/admin') ? 'User control' : t('overview');

  return (
    <div className="app-layout">
      <aside className={`sidebar ${open ? 'sidebar-open' : ''}`}>
        <div className="brand"><span className="brand-mark">K</span><span>khata<span className="brand-dot">.</span></span></div>
        <div className="sidebar-label">{t('workspace')}</div>
        <nav className="side-nav">
          <NavLink to="/dashboard" onClick={() => setOpen(false)}><i className="bi bi-grid-1x2-fill" /> {t('overview')}</NavLink>
          <NavLink to="/transactions" onClick={() => setOpen(false)}><i className="bi bi-arrow-left-right" /> {t('transactions')}</NavLink>
          <NavLink to="/settings" onClick={() => setOpen(false)}><i className="bi bi-gear" /> {t('settings')} <span className="modern-badge">NEW</span></NavLink>
          {['OWNER', 'ADMIN'].includes(user?.role) && <NavLink to="/admin/users" onClick={() => setOpen(false)}><i className="bi bi-people" /> User control</NavLink>}
        </nav>
        <div className="sidebar-bottom">
          <div className="user-chip"><span className="avatar">{user?.fullName?.charAt(0).toUpperCase()}</span><span className="text-truncate"><strong>{user?.fullName}</strong><small>@{user?.username}</small></span></div>
          <button className="logout-button" onClick={logout} aria-label={t('logout')}><i className="bi bi-box-arrow-right" /></button>
        </div>
      </aside>
      {open && <button className="sidebar-backdrop" onClick={() => setOpen(false)} aria-label="Close menu" />}
      <main className="main-content">
        <header className="topbar">
          <button className="menu-button" onClick={() => setOpen(true)} aria-label="Open menu"><i className="bi bi-list" /></button>
          <div><p className="eyebrow">Your khata</p><h1>{pageTitle}</h1></div>
          <div className="topbar-actions"><button className="theme-button" onClick={() => setDark((value) => !value)} aria-label="Toggle dark mode"><i className={`bi ${dark ? 'bi-sun' : 'bi-moon'}`} /></button><div className="topbar-avatar">{user?.fullName?.charAt(0).toUpperCase()}</div></div>
        </header>
        <div className="content-container"><Outlet /></div>
      </main>
    </div>
  );
}
