import { formatAmount, formatDate } from '../utils/format';
import { useAuth } from '../context/AuthContext';

export default function TransactionRow({ transaction, onDelete }) {
  const { user } = useAuth();
  const incoming = transaction.type === 'GOT';
  const currency = transaction.currency || user?.currency || 'INR';
  return <div className="transaction-row"><span className={`transaction-icon ${incoming ? 'incoming' : 'outgoing'}`}><i className={`bi ${incoming ? 'bi-arrow-down-left' : 'bi-arrow-up-right'}`} /></span><div className="transaction-main"><strong>{transaction.partyName}</strong><span>{transaction.category}{transaction.note ? ` · ${transaction.note}` : ''}</span></div><div className={`transaction-amount ${incoming ? 'positive' : 'negative'}`}>{incoming ? '+' : '-'}{formatAmount(transaction.amount, currency)}<small>{formatDate(transaction.createdAt)}</small></div><button className="delete-button" onClick={() => onDelete(transaction)} aria-label={`Delete ${transaction.partyName}`}><i className="bi bi-trash3" /></button></div>;
}
