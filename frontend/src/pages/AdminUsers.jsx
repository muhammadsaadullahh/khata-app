import { useEffect, useState } from 'react';
import { adminApi } from '../api/client';
import { getApiError } from '../utils/format';
import { useToast } from '../context/ToastContext';

const empty = { fullName: '', username: '', email: '', password: '', currency: 'INR' };

export default function AdminUsers() {
  const [users, setUsers] = useState([]);
  const [form, setForm] = useState(empty);
  const [role, setRole] = useState('DEMO');
  const [error, setError] = useState('');
  const { toast } = useToast();
  const load = async () => { try { const { data } = await adminApi.getUsers(); setUsers(data); } catch (err) { setError(getApiError(err)); } };
  useEffect(() => { load(); }, []);
  const create = async (event) => { event.preventDefault(); try { await adminApi.createUser({ account: { ...form, currency: form.currency }, role }); setForm(empty); await load(); toast('User created.'); } catch (err) { setError(getApiError(err)); } };
  return <section className="admin-layout"><div className="panel"><p className="eyebrow">Administration</p><h2>Users</h2><p className="muted">Review roles and create controlled demo or owner accounts.</p><div className="user-table">{users.map((item) => <div className="user-row" key={item.userId}><span className="avatar">{item.fullName?.charAt(0).toUpperCase()}</span><div><strong>{item.fullName}</strong><small>{item.email} · @{item.username}</small></div><span className={`role-badge ${item.role?.toLowerCase()}`}>{item.role}</span></div>)}</div></div><div className="panel"><p className="eyebrow">Create account</p><h3>Managed user</h3>{error && <div className="form-error">{error}</div>}<form className="auth-form" onSubmit={create}><label>Full name<input required value={form.fullName} onChange={(e) => setForm({ ...form, fullName: e.target.value })} /></label><label>Username<input required pattern="[a-zA-Z0-9._-]{3,50}" value={form.username} onChange={(e) => setForm({ ...form, username: e.target.value })} /></label><label>Email<input required type="email" value={form.email} onChange={(e) => setForm({ ...form, email: e.target.value })} /></label><label>Password<input required minLength="8" type="password" value={form.password} onChange={(e) => setForm({ ...form, password: e.target.value })} /></label><label>Role<select value={role} onChange={(e) => setRole(e.target.value)}><option value="DEMO">Demo user</option><option value="USER">Standard user</option><option value="ADMIN">Admin</option><option value="OWNER">Owner</option></select></label><button className="button-primary" type="submit">Create user</button></form></div></section>;
}
