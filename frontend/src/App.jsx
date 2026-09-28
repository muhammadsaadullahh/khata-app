import { Navigate, Route, Routes } from 'react-router-dom';
import AppShell from './components/AppShell';
import ProtectedRoute from './components/ProtectedRoute';
import Dashboard from './pages/Dashboard';
import Login from './pages/Login';
import Register from './pages/Register';
import Transactions from './pages/Transactions';
import Settings from './pages/Settings';
import AdminUsers from './pages/AdminUsers';

export default function App() {
  return <Routes><Route path="/login" element={<Login />} /><Route path="/register" element={<Register />} /><Route element={<ProtectedRoute />}><Route element={<AppShell />}><Route path="/dashboard" element={<Dashboard />} /><Route path="/transactions" element={<Transactions />} /><Route path="/settings" element={<Settings />} /><Route path="/admin/users" element={<AdminUsers />} /></Route></Route><Route path="*" element={<Navigate to="/dashboard" replace />} /></Routes>;
}
