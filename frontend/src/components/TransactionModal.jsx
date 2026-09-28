import { useEffect, useState } from 'react';
import { khataApi } from '../api/client';
import { useAuth } from '../context/AuthContext';
import { getCurrencySymbol } from '../utils/format';

export default function TransactionModal({ onClose, onSubmit, submitting }) {
  const { user } = useAuth();
  const [form, setForm] = useState(() => ({
    type: 'GOT', amount: '', category: '', partyName: '', note: '', currency: user?.currency || 'INR',
  }));
  const [error, setError] = useState('');
  const [categories, setCategories] = useState([]);
  const [customCategory, setCustomCategory] = useState('');

  useEffect(() => { khataApi.getCategories().then(({ data }) => setCategories(data)).catch(() => {}); }, []);
  const update = (event) => setForm({ ...form, [event.target.name]: event.target.value });
  const submit = async (event) => {
    event.preventDefault();
    if (!form.amount || Number(form.amount) <= 0 || !form.category.trim() || !form.partyName.trim()) {
      setError('Add an amount, category, and person to continue.');
      return;
    }
    try { await onSubmit({ ...form, amount: Number(form.amount) }); } catch (err) { setError(err.message); }
  };
  return <div className="modal-backdrop-custom"><div className="transaction-modal">
    <div className="modal-heading"><div><p className="eyebrow">New entry</p><h2>Add transaction</h2></div><button className="icon-button" onClick={onClose} aria-label="Close"><i className="bi bi-x-lg" /></button></div>
    <div className="type-toggle"><button type="button" className={form.type === 'GOT' ? 'active in' : ''} onClick={() => setForm({ ...form, type: 'GOT' })}><i className="bi bi-arrow-down-left" /> Cash in</button><button type="button" className={form.type === 'GAVE' ? 'active out' : ''} onClick={() => setForm({ ...form, type: 'GAVE' })}><i className="bi bi-arrow-up-right" /> Cash out</button></div>
    <form onSubmit={submit}><div className="amount-field"><span>{getCurrencySymbol(form.currency)}</span><input name="amount" type="number" min="0.01" step="0.01" placeholder="0.00" value={form.amount} onChange={update} autoFocus /></div>
      <div className="form-grid"><label>Category<select name="category" value={form.category} onChange={update}><option value="">Select category</option>{categories.map((category) => <option key={category.categoryId}>{category.name}</option>)}</select><span className="optional">or</span><input maxLength="80" placeholder="Create a category" value={customCategory} onChange={(e) => setCustomCategory(e.target.value)} onBlur={async () => { if (!customCategory.trim()) return; try { const { data } = await khataApi.createCategory({ name: customCategory.trim() }); setCategories((items) => [...items, data]); setForm((current) => ({ ...current, category: data.name })); setCustomCategory(''); } catch { /* validation occurs on submit */ } }} /></label><label>Person / business<input name="partyName" maxLength="200" placeholder="e.g. Rahul" value={form.partyName} onChange={update} /></label></div>
      <div className="form-grid"><label>Currency<select name="currency" value={form.currency} onChange={update}>{['INR', 'USD', 'EUR', 'GBP', 'PKR', 'AED', 'SAR', 'CAD', 'AUD', 'JPY'].map((currency) => <option key={currency}>{currency}</option>)}</select></label><span /></div>
      <label>Note <span className="optional">(optional)</span><textarea name="note" maxLength="500" rows="2" placeholder="Add a little context..." value={form.note} onChange={update} /></label>
      {error && <div className="form-error"><i className="bi bi-exclamation-circle" />{error}</div>}
      <button className="button-primary w-100 mt-2" disabled={submitting}>{submitting ? 'Saving...' : 'Save transaction'} <i className="bi bi-arrow-right" /></button>
    </form>
  </div></div>;
}
