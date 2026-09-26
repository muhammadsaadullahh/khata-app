import { createContext, useCallback, useContext, useMemo, useState } from 'react';
const ToastContext = createContext(null);
export function ToastProvider({ children }) {
  const [toasts, setToasts] = useState([]);
  const toast = useCallback((message, type = 'success') => { const id = Date.now() + Math.random(); setToasts((items) => [...items, { id, message, type }]); window.setTimeout(() => setToasts((items) => items.filter((item) => item.id !== id)), 3500); }, []);
  const value = useMemo(() => ({ toast }), [toast]);
  return <ToastContext.Provider value={value}>{children}<div className="toast-stack" role="status">{toasts.map((item) => <div className={`toast-item ${item.type}`} key={item.id}>{item.message}</div>)}</div></ToastContext.Provider>;
}
export const useToast = () => useContext(ToastContext);
