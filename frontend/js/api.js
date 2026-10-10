/* BorrowBox Centralized API Client */

const getApiBaseUrl = () => {
  if (typeof BORROWBOX_CONFIG !== 'undefined' && BORROWBOX_CONFIG.getApiBaseUrl) {
    return BORROWBOX_CONFIG.getApiBaseUrl();
  }
  return window.location.origin.includes('http')
    ? window.location.origin + '/api/v1'
    : 'http://localhost:8080/api/v1';
};

class ApiClient {
  static get baseApiUrl() {
    return getApiBaseUrl();
  }

  static getAuthHeader() {
    const token = localStorage.getItem('borrowbox_token');
    return token ? { 'Authorization': `Bearer ${token}` } : {};
  }

  static resolveImageUrl(path) {
    if (typeof BORROWBOX_CONFIG !== 'undefined' && BORROWBOX_CONFIG.resolveImageUrl) {
      return BORROWBOX_CONFIG.resolveImageUrl(path);
    }
    return path || 'https://images.unsplash.com/photo-1516035069371-29a1b244cc32?auto=format&fit=crop&w=800&q=80';
  }

  static async request(endpoint, options = {}) {
    const baseUrl = this.baseApiUrl;
    const url = endpoint.startsWith('http') ? endpoint : `${baseUrl}${endpoint}`;
    
    const headers = {
      ...this.getAuthHeader(),
      ...options.headers
    };

    if (!(options.body instanceof FormData) && !headers['Content-Type']) {
      headers['Content-Type'] = 'application/json';
    }

    try {
      const response = await fetch(url, {
        ...options,
        headers
      });

      const contentType = response.headers.get('content-type');
      let data = {};
      if (contentType && contentType.includes('application/json')) {
        data = await response.json();
      }

      if (!response.ok) {
        if (response.status === 401 && !endpoint.includes('/auth/login')) {
          Auth.logout();
          UI.showToast('Session expired. Please log in again.', 'warning');
          window.location.href = 'login.html';
          throw new Error('Unauthorized');
        }
        let errorMsg = data.message;
        if (!errorMsg) {
          if (response.status === 502 || response.status === 503 || response.status === 504) {
            errorMsg = `Backend server is unavailable or starting up (HTTP ${response.status}). Please try again shortly.`;
          } else if (response.status === 403) {
            errorMsg = 'Access forbidden. You do not have permission to perform this action.';
          } else if (response.status === 404) {
            errorMsg = `Requested resource was not found (HTTP 404: ${endpoint}).`;
          } else {
            errorMsg = `Request failed with status ${response.status}`;
          }
        }
        if (data.data && typeof data.data === 'object' && Object.values(data.data).length > 0) {
          errorMsg = Object.values(data.data).join(', ');
        }
        throw new Error(errorMsg);
      }

      return data.data !== undefined ? data.data : data;
    } catch (error) {
      console.error(`API Error [${endpoint}]:`, error);
      if (error instanceof TypeError && (error.message.includes('fetch') || error.message.includes('NetworkError'))) {
        throw new Error(`Unable to connect to BorrowBox API at ${baseUrl}. Please check that the server is online and CORS is configured.`);
      }
      throw error;
    }
  }

  static get(endpoint) {
    return this.request(endpoint, { method: 'GET' });
  }

  static post(endpoint, body) {
    const isFormData = body instanceof FormData;
    return this.request(endpoint, {
      method: 'POST',
      body: isFormData ? body : JSON.stringify(body)
    });
  }

  static put(endpoint, body) {
    return this.request(endpoint, {
      method: 'PUT',
      body: JSON.stringify(body)
    });
  }

  static patch(endpoint, body) {
    return this.request(endpoint, {
      method: 'PATCH',
      body: JSON.stringify(body)
    });
  }

  static delete(endpoint) {
    return this.request(endpoint, { method: 'DELETE' });
  }
}
