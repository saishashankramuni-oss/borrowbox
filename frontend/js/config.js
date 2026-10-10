/**
 * BorrowBox Environment Configuration
 * Handles automatic API routing, environment detection, and image resolution.
 */

// 1. Check URL query param (?backend=https://...) to allow easy one-time configuration
if (typeof window !== 'undefined' && window.location && window.location.search) {
  try {
    const params = new URLSearchParams(window.location.search);
    const backendParam = params.get('backend');
    if (backendParam && backendParam.startsWith('http')) {
      const cleanParam = backendParam.replace(/\/+$/, '');
      localStorage.setItem('BORROWBOX_BACKEND_URL', cleanParam);
      // Clean query parameter from URL without reload
      const newUrl = window.location.pathname + window.location.hash;
      window.history.replaceState({}, document.title, newUrl);
    }
  } catch (e) {
    // Ignore URL parse errors
  }
}

const BORROWBOX_CONFIG = {
  // Configured backend URL (from window global, localStorage, or hardcoded for direct domain deployment)
  BACKEND_URL: (typeof window !== 'undefined' && (window.BORROWBOX_BACKEND_URL || localStorage.getItem('BORROWBOX_BACKEND_URL'))) || '',

  /**
   * Set and persist the backend URL (e.g. from browser console or settings)
   */
  setBackendUrl(url) {
    if (typeof window === 'undefined') return;
    if (url && url.trim() !== '') {
      const clean = url.trim().replace(/\/+$/, '');
      this.BACKEND_URL = clean;
      localStorage.setItem('BORROWBOX_BACKEND_URL', clean);
      console.log('[BorrowBox] Backend URL configured to:', clean);
    } else {
      this.BACKEND_URL = '';
      localStorage.removeItem('BORROWBOX_BACKEND_URL');
      console.log('[BorrowBox] Backend URL reset to automatic detection');
    }
  },

  /**
   * Determine the API base URL based on runtime environment
   */
  getApiBaseUrl() {
    // 1. Explicitly configured backend URL (takes precedence for multi-origin deployments)
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

    // 3. Deployed site with relative path (Single Spring Boot app or Netlify proxy)
    if (typeof window !== 'undefined' && window.location && window.location.origin && window.location.origin.startsWith('http')) {
      return `${window.location.origin}/api/v1`;
    }

    // 4. Default fallback
    return 'http://localhost:8080/api/v1';
  },

  /**
   * Resolve an image file path or URL to an absolute displayable URL
   */
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
  window.setBackendUrl = (url) => BORROWBOX_CONFIG.setBackendUrl(url);
}
