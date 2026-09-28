const currencyLocales = {
  INR: 'en-IN', USD: 'en-US', EUR: 'de-DE', GBP: 'en-GB', PKR: 'ur-PK',
  AED: 'ar-AE', SAR: 'ar-SA', CAD: 'en-CA', AUD: 'en-AU', JPY: 'ja-JP',
};

export const formatAmount = (value, currency = 'INR', locale = currencyLocales[currency] || 'en-US') => {
  const code = String(currency || 'INR').toUpperCase();
  try {
    return new Intl.NumberFormat(locale, {
      style: 'currency', currency: code, maximumFractionDigits: 2, minimumFractionDigits: 2,
    }).format(Number(value) || 0);
  } catch {
    return `${code} ${(Number(value) || 0).toFixed(2)}`;
  }
};
export const getCurrencySymbol = (currency = 'INR', locale = currencyLocales[currency] || 'en-US') => {
  const code = String(currency || 'INR').toUpperCase();
  try {
    return new Intl.NumberFormat(locale, { style: 'currency', currency: code, currencyDisplay: 'narrowSymbol' })
      .formatToParts(0).find((part) => part.type === 'currency')?.value || currency;
  } catch {
    return currency;
  }
};
export const formatDate = (value, locale = 'en-IN') => new Intl.DateTimeFormat(locale, { day: '2-digit', month: 'short', year: 'numeric' }).format(new Date(value));
export const formatShortDate = (value, locale = 'en-IN') => new Intl.DateTimeFormat(locale, { day: '2-digit', month: 'short' }).format(new Date(value));
export const getApiError = (error) => error.response?.data?.detail || error.response?.data?.message || 'Something went wrong. Please try again.';
