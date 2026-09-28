import { useState } from 'react';
import { Link, Navigate, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { getApiError } from '../utils/format';
import { useI18n } from '../context/I18nContext';
import { useToast } from '../context/ToastContext';

const passwordPattern = /^(?=\S{8,72}$)(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[^A-Za-z\d]).*$/;

export default function Register() {
  const { user, register, loading } = useAuth();
  const { t } = useI18n();
  const { toast } = useToast();
  const navigate = useNavigate();
  const [error, setError] = useState('');
  const [form, setForm] = useState({
    fullName: '', username: '', email: '', password: '', confirmPassword: '', currency: 'INR',
  });
  const [showPassword, setShowPassword] = useState(false);
  const [showConfirmPassword, setShowConfirmPassword] = useState(false);
  if (user) return <Navigate to="/dashboard" replace />;
  const update = (event) => setForm({ ...form, [event.target.name]: event.target.value });
  const submit = async (event) => {
    event.preventDefault();
    if (form.fullName.trim().length < 2
      || !/^[a-zA-Z0-9._-]{3,50}$/.test(form.username)
      || !/^[^\s@]+@[^\s@]+\.[^\s@]{2,}$/.test(form.email)
      || !passwordPattern.test(form.password)
      || form.password !== form.confirmPassword) {
      setError('Enter a valid email and use 8-72 characters with upper/lowercase, a number and symbol; passwords must match.');
      return;
    }
    try {
      const { confirmPassword, ...request } = form;
      await register(request);
      toast('Account created successfully. Please sign in.');
      navigate('/login', { replace: true, state: { registered: true } });
    } catch (err) {
      setError(getApiError(err));
    }
  };
  const checks = [
    form.password.length >= 8,
    /[a-z]/.test(form.password),
    /[A-Z]/.test(form.password),
    /\d/.test(form.password),
    /[^A-Za-z\d]/.test(form.password),
  ];
  const strength = checks.filter(Boolean).length;
  return <AuthLayout><p className="eyebrow">{t('register')}</p><h1>Make it count.</h1><p className="auth-subtitle">Set up your private money space in less than a minute.</p>{error && <div className="form-error">{error}</div>}<form onSubmit={submit} className="auth-form">
    <label>Full name<input name="fullName" value={form.fullName} onChange={update} placeholder="Your full name" maxLength="120" autoComplete="name" /></label>
    <div className="form-grid"><label>Username<input name="username" value={form.username} onChange={update} placeholder="e.g. anita_24" maxLength="50" autoComplete="username" /></label><label>Email<input type="email" name="email" value={form.email} onChange={update} placeholder="you@example.com" maxLength="254" autoComplete="email" /></label></div>
    <label>Password<div className="password-field"><input type={showPassword ? 'text' : 'password'} name="password" value={form.password} onChange={update} placeholder="8+ characters" maxLength="72" autoComplete="new-password" /><button type="button" onClick={() => setShowPassword(!showPassword)} aria-label={showPassword ? 'Hide password' : 'Show password'}><i className={`bi bi-eye${showPassword ? '-slash' : ''}`} /></button></div><div className="strength-meter" aria-label={`Password strength ${strength} of 5`}><span style={{ width: `${strength * 20}%` }} /></div><small className="muted">Use uppercase, lowercase, number and symbol</small></label>
    <label>Confirm password<div className={`password-field ${form.confirmPassword ? (form.password === form.confirmPassword ? 'match' : 'mismatch') : ''}`}><input type={showConfirmPassword ? 'text' : 'password'} name="confirmPassword" value={form.confirmPassword} onChange={update} autoComplete="new-password" /><button type="button" onClick={() => setShowConfirmPassword(!showConfirmPassword)} aria-label={showConfirmPassword ? 'Hide confirmation password' : 'Show confirmation password'}><i className={`bi bi-eye${showConfirmPassword ? '-slash' : ''}`} /></button></div>{form.confirmPassword && <small className={form.password === form.confirmPassword ? 'match-text' : 'mismatch-text'}>{form.password === form.confirmPassword ? 'Passwords match' : 'Passwords do not match'}</small>}</label>
    <label>Preferred currency<select name="currency" value={form.currency} onChange={update}>{['INR', 'USD', 'EUR', 'GBP', 'PKR', 'AED', 'SAR', 'CAD', 'AUD', 'JPY'].map((currency) => <option key={currency}>{currency}</option>)}</select></label>
    <button className="button-primary w-100" disabled={loading}>{loading ? 'Creating account...' : 'Create account'} <i className="bi bi-arrow-right" /></button>
  </form><p className="auth-switch">Already have an account? <Link to="/login">Sign in</Link></p></AuthLayout>;
}

function AuthLayout({ children }) {
  return <div className="auth-page"><div className="auth-decoration"><div className="brand"><span className="brand-mark">K</span><span>khata<span className="brand-dot">.</span></span></div><div className="decoration-copy"><h2>Know where<br /><em>you stand.</em></h2><p>A calmer way to keep track of every give, get, and goal.</p></div><div className="decoration-orb orb-one" /><div className="decoration-orb orb-two" /></div><main className="auth-card"><div className="mobile-brand brand"><span className="brand-mark">K</span><span>khata<span className="brand-dot">.</span></span></div>{children}<p className="auth-legal">By continuing, you agree to keep your account secure.</p></main></div>;
}
