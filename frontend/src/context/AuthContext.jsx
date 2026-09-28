import { createContext, useContext, useMemo, useState } from 'react';
import { authApi, TOKEN_KEY } from '../api/client';
import { useToast } from './ToastContext';

const AuthContext = createContext(null);

function readUser() {
  try {
    return JSON.parse(localStorage.getItem('khata_user')) || null;
  } catch {
    return null;
  }
}

export function AuthProvider({ children }) {
  const [user, setUser] = useState(readUser);
  const [loading, setLoading] = useState(false);
  const { toast, confirm } = useToast();

  const saveSession = (data) => {
    localStorage.setItem(TOKEN_KEY, data.token);
    localStorage.setItem('khata_user', JSON.stringify(data.user));
    setUser(data.user);
  };

  const login = async (payload) => {
    setLoading(true);
    try {
      const { data } = await authApi.login(payload);
      saveSession(data);
      return data;
    } catch (error) {
      toast(error.response?.data?.detail || 'Unable to sign in.', 'error');
      throw error;
    } finally {
      setLoading(false);
    }
  };

  const register = async (payload) => {
    setLoading(true);
    try {
      const { data } = await authApi.register(payload);
      return data;
    } catch (error) {
      toast(error.response?.data?.detail || 'Unable to create account.', 'error');
      throw error;
    } finally {
      setLoading(false);
    }
  };

  const logout = async () => {
    if (!await confirm('Log out of Khata?', 'Your current session will be ended on this device.')) return;
    localStorage.removeItem(TOKEN_KEY);
    localStorage.removeItem('khata_user');
    setUser(null);
  };

  const updatePreferences = async (payload) => {
    const { data } = await authApi.updatePreferences(payload);
    localStorage.setItem('khata_user', JSON.stringify(data));
    setUser(data);
    return data;
  };
  const updateProfile = async (payload) => {
    const { data } = await authApi.updateProfile(payload);
    localStorage.setItem('khata_user', JSON.stringify(data)); setUser(data); return data;
  };
  const value = useMemo(() => ({ user, loading, login, register, updatePreferences, updateProfile, logout }), [user, loading, confirm]);
  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  return useContext(AuthContext);
}
