import { createContext, useCallback, useContext, useMemo, useState } from 'react';
const ToastContext = createContext(null);
export function ToastProvider({ children }) {
  const [toasts, setToasts] = useState([]);
  const [confirmation, setConfirmation] = useState(null);
  const toast = useCallback((message, type = 'success') => { const id = Date.now() + Math.random(); setToasts((items) => [...items, { id, message, type }]); window.setTimeout(() => setToasts((items) => items.filter((item) => item.id !== id)), 3500); }, []);
  const confirm = useCallback((title, message) => new Promise((resolve) => setConfirmation({ title, message, resolve })), []);
  const closeConfirmation = (result) => { confirmation?.resolve(result); setConfirmation(null); };
  const value = useMemo(() => ({ toast, confirm }), [toast, confirm]);
  return <ToastContext.Provider value={value}>{children}<div className="toast-stack" role="status">{toasts.map((item) => <div className={`toast-item ${item.type}`} key={item.id}>{item.message}</div>)}</div>{confirmation && <div className="confirm-backdrop" role="presentation"><section className="confirm-dialog" role="alertdialog" aria-modal="true" aria-labelledby="confirm-title"><span className="confirm-icon"><i className="bi bi-shield-exclamation" /></span><h2 id="confirm-title">{confirmation.title}</h2><p>{confirmation.message}</p><div className="confirm-actions"><button className="button-secondary" onClick={() => closeConfirmation(false)}>Cancel</button><button className="button-danger" onClick={() => closeConfirmation(true)}>Continue</button></div></section></div>}</ToastContext.Provider>;
}
export const useToast = () => useContext(ToastContext);
