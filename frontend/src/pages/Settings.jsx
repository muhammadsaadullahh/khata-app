import { useState } from 'react';
import { useAuth } from '../context/AuthContext';
import { getApiError } from '../utils/format';
import { languageNames, useI18n } from '../context/I18nContext';
import { useToast } from '../context/ToastContext';

export default function Settings() {
  const { user, updatePreferences, updateProfile } = useAuth();
  const [currency, setCurrency] = useState(user?.currency || 'INR');
  const { t, language, setLanguage } = useI18n(); const { toast } = useToast();
  const [message, setMessage] = useState('');
  const [profile, setProfile] = useState({ fullName: user?.fullName || '', username: user?.username || '', email: user?.email || '', password: '' });
  const save = async (event) => { event.preventDefault(); try { await updatePreferences({ currency }); setMessage(t('preferencesSaved')); toast(t('preferencesSaved')); } catch (error) { const detail = getApiError(error); setMessage(detail); toast(detail, 'error'); } };
  const saveProfile = async (event) => { event.preventDefault(); try { await updateProfile(profile); setProfile((p) => ({ ...p, password: '' })); toast('Profile updated.'); } catch (error) { toast(getApiError(error), 'error'); } };
  const demo = user?.role === 'DEMO';
  return <section className="panel settings-panel"><p className="eyebrow">{t('profile')}</p><h2>{t('settings')}</h2><p className="muted">Personalise your account and money display.</p>{demo && <div className="info-alert">Demo accounts can update currency only.</div>}{message && <div className="info-alert">{message}</div>}<form onSubmit={saveProfile} className="auth-form"><label>Full name<input disabled={demo} value={profile.fullName} onChange={(e) => setProfile({ ...profile, fullName: e.target.value })} /></label><label>Username<input disabled={demo} value={profile.username} onChange={(e) => setProfile({ ...profile, username: e.target.value })} /></label><label>Email<input disabled={demo} type="email" value={profile.email} onChange={(e) => setProfile({ ...profile, email: e.target.value })} /></label><label>New password<input disabled={demo} type="password" value={profile.password} onChange={(e) => setProfile({ ...profile, password: e.target.value })} /></label>{!demo && <button className="button-primary" type="submit">Update profile</button>}</form><form onSubmit={save} className="auth-form"><label>{t('currency')}<select value={currency} onChange={(e) => setCurrency(e.target.value)}>{['INR','USD','EUR','GBP','PKR','AED','SAR','CAD','AUD','JPY'].map((item) => <option key={item}>{item}</option>)}</select></label><label>{t('language')}<select value={language} onChange={(e) => setLanguage(e.target.value)}>{Object.entries(languageNames).map(([key, value]) => <option key={key} value={key}>{value}</option>)}</select></label><button className="button-primary" type="submit">{t('save')}</button></form></section>;
}
