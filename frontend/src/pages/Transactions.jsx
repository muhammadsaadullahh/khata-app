import { useEffect, useMemo, useState } from 'react';
import { khataApi } from '../api/client';
import { useAuth } from '../context/AuthContext';
import TransactionModal from '../components/TransactionModal';
import TransactionRow from '../components/TransactionRow';
import { getApiError } from '../utils/format';
import { useToast } from '../context/ToastContext';

export default function Transactions() {
  const { user } = useAuth(); const { toast } = useToast(); const [items, setItems] = useState([]); const [query, setQuery] = useState(''); const [filter, setFilter] = useState('ALL'); const [modal, setModal] = useState(false); const [error, setError] = useState('');
  const load = async () => { try { const { data } = await khataApi.getTransactions(user.userId); setItems(data); } catch (err) { setError(getApiError(err)); } };
  useEffect(() => { load(); }, [user.userId]);
  const filtered = useMemo(() => items.filter((item) => (filter === 'ALL' || item.type === filter) && [item.partyName, item.category, item.note].some((part) => part?.toLowerCase().includes(query.toLowerCase()))), [items, query, filter]);
  const add = async (payload) => { try { const { data } = await khataApi.createTransaction(payload); setItems((current) => [data, ...current]); setModal(false); toast('Transaction added successfully.'); } catch (err) { const message = getApiError(err); toast(message, 'error'); throw new Error(message); } };
  const remove = async (item) => { if (!window.confirm(`Delete this transaction with ${item.partyName}?`)) return; try { await khataApi.deleteTransaction(user.userId, item.transactionId); setItems((current) => current.filter((entry) => entry.transactionId !== item.transactionId)); toast('Transaction deleted.'); } catch (err) { const message = getApiError(err); setError(message); toast(message, 'error'); } };
  return <>{error && <div className="page-alert form-error">{error}</div>}<section className="welcome-row"><div><p className="eyebrow">Money trail</p><h2>All transactions</h2><p className="muted">Every give and get, in one place.</p></div><button className="button-primary" onClick={() => setModal(true)}><i className="bi bi-plus-lg" /> Add transaction</button></section><section className="panel"><div className="transaction-toolbar"><div className="search-box"><i className="bi bi-search" /><input value={query} onChange={(e) => setQuery(e.target.value)} placeholder="Search transactions" /></div><div className="filter-tabs"><button className={filter === 'ALL' ? 'active' : ''} onClick={() => setFilter('ALL')}>All</button><button className={filter === 'GOT' ? 'active' : ''} onClick={() => setFilter('GOT')}>Cash in</button><button className={filter === 'GAVE' ? 'active' : ''} onClick={() => setFilter('GAVE')}>Cash out</button></div></div>{filtered.length ? filtered.map((item) => <TransactionRow key={item.transactionId} transaction={item} onDelete={remove} />) : <div className="empty-state"><span className="empty-icon"><i className="bi bi-search" /></span><strong>No matches found</strong><span>Try a different search or filter.</span></div>}</section>{modal && <TransactionModal onClose={() => setModal(false)} onSubmit={add} />}</>;
}
