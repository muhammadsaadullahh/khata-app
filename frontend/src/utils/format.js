export const formatAmount = (value, currency = 'INR', locale = 'en-IN') => new Intl.NumberFormat(locale, { style: 'currency', currency, maximumFractionDigits: 2, minimumFractionDigits: 2 }).format(Number(value) || 0);
export const formatDate = (value, locale = 'en-IN') => new Intl.DateTimeFormat(locale, { day: '2-digit', month: 'short', year: 'numeric' }).format(new Date(value));
export const formatShortDate = (value, locale = 'en-IN') => new Intl.DateTimeFormat(locale, { day: '2-digit', month: 'short' }).format(new Date(value));
export const getApiError = (error) => error.response?.data?.detail || error.response?.data?.message || 'Something went wrong. Please try again.';
