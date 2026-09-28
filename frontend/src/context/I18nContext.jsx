import { createContext, useContext, useEffect, useMemo, useState } from 'react';

const catalogs = {
  en: { welcome: 'Welcome back', register: 'Create account', overview: 'Overview', transactions: 'Transactions', settings: 'Settings', workspace: 'Workspace', profile: 'Profile', save: 'Save preferences', currency: 'Currency', language: 'Language', preferencesSaved: 'Preferences saved.', logout: 'Log out' },
  ur: { welcome: 'خوش آمدید', register: 'اکاؤنٹ بنائیں', overview: 'جائزہ', transactions: 'لین دین', settings: 'ترتیبات', workspace: 'ورک اسپیس', profile: 'پروفائل', save: 'ترجیحات محفوظ کریں', currency: 'کرنسی', language: 'زبان', preferencesSaved: 'ترجیحات محفوظ ہو گئیں۔', logout: 'لاگ آؤٹ' },
  hi: { welcome: 'वापसी पर स्वागत है', register: 'खाता बनाएं', overview: 'अवलोकन', transactions: 'लेन-देन', settings: 'सेटिंग्स', workspace: 'कार्यस्थान', profile: 'प्रोफ़ाइल', save: 'प्राथमिकताएं सहेजें', currency: 'मुद्रा', language: 'भाषा', preferencesSaved: 'प्राथमिकताएं सहेजी गईं।', logout: 'लॉग आउट' },
  ar: { welcome: 'مرحباً بعودتك', register: 'إنشاء حساب', overview: 'نظرة عامة', transactions: 'المعاملات', settings: 'الإعدادات', workspace: 'مساحة العمل', profile: 'الملف الشخصي', save: 'حفظ التفضيلات', currency: 'العملة', language: 'اللغة', preferencesSaved: 'تم حفظ التفضيلات.', logout: 'تسجيل الخروج' },
  es: { welcome: 'Bienvenido de nuevo', register: 'Crear cuenta', overview: 'Resumen', transactions: 'Transacciones', settings: 'Configuración', workspace: 'Espacio de trabajo', profile: 'Perfil', save: 'Guardar preferencias', currency: 'Moneda', language: 'Idioma', preferencesSaved: 'Preferencias guardadas.', logout: 'Cerrar sesión' },
  fr: { welcome: 'Bon retour', register: 'Créer un compte', overview: 'Vue d’ensemble', transactions: 'Transactions', settings: 'Paramètres', workspace: 'Espace de travail', profile: 'Profil', save: 'Enregistrer', currency: 'Devise', language: 'Langue', preferencesSaved: 'Préférences enregistrées.', logout: 'Se déconnecter' },
  de: { welcome: 'Willkommen zurück', register: 'Konto erstellen', overview: 'Übersicht', transactions: 'Transaktionen', settings: 'Einstellungen', workspace: 'Arbeitsbereich', profile: 'Profil', save: 'Einstellungen speichern', currency: 'Währung', language: 'Sprache', preferencesSaved: 'Einstellungen gespeichert.', logout: 'Abmelden' },
  tr: { welcome: 'Tekrar hoş geldiniz', register: 'Hesap oluştur', overview: 'Genel bakış', transactions: 'İşlemler', settings: 'Ayarlar', workspace: 'Çalışma alanı', profile: 'Profil', save: 'Tercihleri kaydet', currency: 'Para birimi', language: 'Dil', preferencesSaved: 'Tercihler kaydedildi.', logout: 'Çıkış yap' },
};
export const languageNames = { en: 'English', ur: 'اردو', hi: 'हिन्दी', ar: 'العربية', es: 'Español', fr: 'Français', de: 'Deutsch', tr: 'Türkçe' };
const I18nContext = createContext(null);
export function I18nProvider({ children }) {
  const storedLanguage = localStorage.getItem('khata_language');
  const [language, setLanguageState] = useState(catalogs[storedLanguage] ? storedLanguage : 'en');
  const setLanguage = (nextLanguage) => setLanguageState(catalogs[nextLanguage] ? nextLanguage : 'en');
  const isRtl = ['ur', 'ar'].includes(language);
  useEffect(() => { localStorage.setItem('khata_language', language); document.documentElement.lang = language; document.documentElement.dir = isRtl ? 'rtl' : 'ltr'; }, [language, isRtl]);
  const value = useMemo(() => ({ language, setLanguage, isRtl, t: (key) => catalogs[language]?.[key] || catalogs.en[key] || key }), [language, isRtl]);
  return <I18nContext.Provider value={value}>{children}</I18nContext.Provider>;
}
export const useI18n = () => useContext(I18nContext);
