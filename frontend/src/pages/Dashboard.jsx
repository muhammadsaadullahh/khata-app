import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { khataApi } from '../api/client';
import { useAuth } from '../context/AuthContext';
import TransactionModal from '../components/TransactionModal';
import TransactionRow from '../components/TransactionRow';
import { formatAmount, formatShortDate, getApiError } from '../utils/format';
import { useToast } from '../context/ToastContext';

export default function Dashboard() {
  const { user } = useAuth(); const { toast } = useToast(); const [summary, setSummary] = useState(null); const [transactions, setTransactions] = useState([]); const [modal, setModal] = useState(false); const [loading, setLoading] = useState(true); const [saving, setSaving] = useState(false); const [error, setError] = useState('');
  const load = async () => { setLoading(true); try { const [sum, list] = await Promise.all([khataApi.getSummary(user.userId), khataApi.getTransactions(user.userId)]); setSummary(sum.data); setTransactions(list.data); } catch (err) { setError(getApiError(err)); } finally { setLoading(false); } };
  useEffect(() => { load(); }, [user.userId]);
  const add = async (payload) => { setSaving(true); try { const { data } = await khataApi.createTransaction(payload); setTransactions((items) => [data, ...items]); setSummary((current) => ({ totalCashIn: Number(current.totalCashIn) + (payload.type === 'GOT' ? payload.amount : 0), totalCashOut: Number(current.totalCashOut) + (payload.type === 'GAVE' ? payload.amount : 0), netBalance: Number(current.netBalance) + (payload.type === 'GOT' ? payload.amount : -payload.amount) })); setModal(false); toast('Transaction added successfully.'); } catch (err) { const message = getApiError(err); toast(message, 'error'); throw new Error(message); } finally { setSaving(false); } };
  const deleteItem = async (transaction) => { if (!window.confirm(`Delete this transaction with ${transaction.partyName}?`)) return; try { await khataApi.deleteTransaction(user.userId, transaction.transactionId); setTransactions((items) => items.filter((item) => item.transactionId !== transaction.transactionId)); await load(); toast('Transaction deleted.'); } catch (err) { const message = getApiError(err); setError(message); toast(message, 'error'); } };
  const latest = transactions.slice(0, 5);
  return <>{error && <div className="page-alert form-error">{error}<button onClick={() => setError('')}><i className="bi bi-x" /></button></div>}<section className="welcome-row"><div><p className="eyebrow">{new Intl.DateTimeFormat('en-IN', { weekday: 'long' }).format(new Date())}, {formatShortDate(new Date())}</p><h2>Good morning, {user.fullName.split(' ')[0]} <span className="wave">✦</span></h2><p className="muted">Here’s how your money is looking.</p></div><button className="button-primary" onClick={() => setModal(true)}><i className="bi bi-plus-lg" /> Add transaction</button></section>
    <div className="summary-grid"><SummaryCard label="Cash in" value={summary?.totalCashIn} icon="bi-arrow-down-left" tone="green" loading={loading} /><SummaryCard label="Cash out" value={summary?.totalCashOut} icon="bi-arrow-up-right" tone="orange" loading={loading} /><SummaryCard label="Net balance" value={summary?.netBalance} icon="bi-wallet2" tone="dark" loading={loading} /></div>
    <section className="panel"><div className="panel-heading"><div><p className="eyebrow">Your activity</p><h3>Recent transactions</h3></div><Link to="/transactions" className="text-link">View all <i className="bi bi-arrow-up-right" /></Link></div>{loading ? <div className="empty-state"><span className="spinner-border spinner-border-sm" /> Loading your activity...</div> : latest.length ? latest.map((transaction) => <TransactionRow key={transaction.transactionId} transaction={transaction} onDelete={deleteItem} />) : <EmptyState onAdd={() => setModal(true)} />}</section>
    {modal && <TransactionModal onClose={() => setModal(false)} onSubmit={add} submitting={saving} />}
  </>;
}
function SummaryCard({ label, value, icon, tone, loading }) { const { user } = useAuth(); return <div className={`summary-card ${tone}`}><div className="summary-top"><span>{label}</span><i className={`bi ${icon}`} /></div>{loading ? <div className="skeleton skeleton-number" /> : <strong>{formatAmount(value, user?.currency)}</strong>}<small>{label === 'Net balance' ? 'Cash in minus cash out' : 'All time'}</small></div>; }
function EmptyState({ onAdd }) { return <div className="empty-state"><span className="empty-icon"><i className="bi bi-receipt" /></span><strong>No transactions yet</strong><span>Start by recording a cash in or cash out.</span><button className="text-link" onClick={onAdd}>Add your first one <i className="bi bi-arrow-right" /></button></div>; }
