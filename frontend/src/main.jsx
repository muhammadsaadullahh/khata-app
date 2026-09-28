import React from 'react';
import ReactDOM from 'react-dom/client';
import { BrowserRouter } from 'react-router-dom';
import 'bootstrap/dist/css/bootstrap.min.css';
import 'bootstrap-icons/font/bootstrap-icons.css';
import './styles.css';
import App from './App';
import { AuthProvider } from './context/AuthContext';
import { I18nProvider } from './context/I18nContext';
import { ToastProvider } from './context/ToastContext';

ReactDOM.createRoot(document.getElementById('root')).render(<React.StrictMode><BrowserRouter><I18nProvider><ToastProvider><AuthProvider><App /></AuthProvider></ToastProvider></I18nProvider></BrowserRouter></React.StrictMode>);
