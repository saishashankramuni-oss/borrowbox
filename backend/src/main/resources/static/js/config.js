/**
 * BorrowBox Environment Configuration
 */
const BORROWBOX_CONFIG = {
  // Set your deployed Render backend URL here (e.g. 'https://borrowbox-backend.onrender.com')
  // Or leave as empty string '' if using Netlify proxy redirects / same-origin deployment
  BACKEND_URL: (typeof window !== 'undefined' && (window.BORROWBOX_BACKEND_URL || localStorage.getItem('BORROWBOX_BACKEND_URL'))) || '',

  getApiBaseUrl() {
    // 1. Explicit backend URL
    if (this.BACKEND_URL && this.BACKEND_URL.trim() !== '') {
      const base = this.BACKEND_URL.replace(/\/+$/, '');
      return `${base}/api/v1`;
    }

    // 2. Local dev server (Live Server on port 5500, etc.)
    if (typeof window !== 'undefined' && window.location && (window.location.hostname === 'localhost' || window.location.hostname === '127.0.0.1')) {
      if (window.location.port !== '8080') {
        return 'http://localhost:8080/api/v1';
      }
    }

    // 3. Deployed site with relative path (Netlify proxy or Spring Boot static site)
    if (typeof window !== 'undefined' && window.location && window.location.origin && window.location.origin.startsWith('http')) {
      return `${window.location.origin}/api/v1`;
    }

    // 4. Default fallback
    return 'http://localhost:8080/api/v1';
  },

  resolveImageUrl(path) {
    const defaultPlaceholder = 'https://images.unsplash.com/photo-1516035069371-29a1b244cc32?auto=format&fit=crop&w=800&q=80';
    if (!path) return defaultPlaceholder;
    if (path.startsWith('http://') || path.startsWith('https://')) {
      return path;
    }
    const cleanPath = path.startsWith('/') ? path : `/${path}`;
    if (this.BACKEND_URL && this.BACKEND_URL.trim() !== '') {
      return `${this.BACKEND_URL.replace(/\/+$/, '')}${cleanPath}`;
    }
    return cleanPath;
  }
};

if (typeof window !== 'undefined') {
  window.BORROWBOX_CONFIG = BORROWBOX_CONFIG;
}
