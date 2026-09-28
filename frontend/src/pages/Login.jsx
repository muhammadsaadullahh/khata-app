import { useState } from 'react';
import { Link, Navigate, useLocation, useNavigate, useSearchParams } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { getApiError } from '../utils/format';
import { useI18n } from '../context/I18nContext';

export default function Login() {
  const { user, login, loading } = useAuth();
  const { t } = useI18n();
  const navigate = useNavigate();
  const location = useLocation();
  const [params] = useSearchParams();
  const [form, setForm] = useState({ username: '', password: '' });
  const [error, setError] = useState('');
  const [showPassword, setShowPassword] = useState(false);
  if (user) return <Navigate to="/dashboard" replace />;
  const submit = async (event) => {
    event.preventDefault();
    if (!form.username.trim() || !form.password) {
      setError('Enter your username and password.');
      return;
    }
    try {
      await login(form);
      navigate(location.state?.from?.pathname || '/dashboard', { replace: true });
    } catch (err) {
      setError(getApiError(err));
    }
  };
  return <AuthLayout><p className="eyebrow">{t('welcome')}</p><h1>Good to see you.</h1><p className="auth-subtitle">Keep your money conversations clear and your records close.</p>{params.get('expired') && <div className="info-alert">Your session expired. Please log in again.</div>}{location.state?.registered && <div className="info-alert">Account created successfully. Please sign in.</div>}{error && <div className="form-error">{error}</div>}<form onSubmit={submit} className="auth-form">
    <label>Username<input value={form.username} onChange={(event) => setForm({ ...form, username: event.target.value })} placeholder="your username" maxLength="50" autoComplete="username" /></label>
    <label>Password<div className="password-field"><input type={showPassword ? 'text' : 'password'} value={form.password} onChange={(event) => setForm({ ...form, password: event.target.value })} placeholder="Your password" maxLength="72" autoComplete="current-password" /><button type="button" onClick={() => setShowPassword(!showPassword)} aria-label={showPassword ? 'Hide password' : 'Show password'}><i className={`bi bi-eye${showPassword ? '-slash' : ''}`} /></button></div></label>
    <button className="button-primary w-100" disabled={loading}>{loading ? 'Signing in...' : 'Sign in'} <i className="bi bi-arrow-right" /></button>
  </form><p className="auth-switch">New to Khata? <Link to="/register">Create an account</Link></p></AuthLayout>;
}

function AuthLayout({ children }) {
  return <div className="auth-page"><div className="auth-decoration"><div className="brand"><span className="brand-mark">K</span><span>khata<span className="brand-dot">.</span></span></div><div className="decoration-copy"><h2>Know where<br /><em>you stand.</em></h2><p>A calmer way to keep track of every give, get, and goal.</p></div><div className="decoration-orb orb-one" /><div className="decoration-orb orb-two" /></div><main className="auth-card"><div className="mobile-brand brand"><span className="brand-mark">K</span><span>khata<span className="brand-dot">.</span></span></div>{children}<p className="auth-legal">By continuing, you agree to keep your account secure.</p></main></div>;
}
