/* BorrowBox Auth Service */

class Auth {
  static getToken() {
    return localStorage.getItem('borrowbox_token');
  }

  static getUser() {
    const userStr = localStorage.getItem('borrowbox_user');
    try {
      return userStr ? JSON.parse(userStr) : null;
    } catch (e) {
      return null;
    }
  }

  static isAuthenticated() {
    return !!this.getToken();
  }

  static saveSession(token, user) {
    localStorage.setItem('borrowbox_token', token);
    localStorage.setItem('borrowbox_user', JSON.stringify(user));
  }

  static async logout() {
    try {
      await ApiClient.post('/auth/logout', {});
    } catch (e) {
      // Ignore network errors during logout
    }
    localStorage.removeItem('borrowbox_token');
    localStorage.removeItem('borrowbox_user');
    window.location.href = 'index.html';
  }

  static async fetchCurrentUser() {
    if (!this.isAuthenticated()) return null;
    try {
      const user = await ApiClient.get('/auth/me');
      localStorage.setItem('borrowbox_user', JSON.stringify(user));
      return user;
    } catch (e) {
      return null;
    }
  }

  static requireAuth() {
    if (!this.isAuthenticated()) {
      UI.showToast('Please login to access this page.', 'warning');
      window.location.href = `login.html?redirect=${encodeURIComponent(window.location.pathname)}`;
      return false;
    }
    return true;
  }

  static requireRole(role) {
    if (!this.requireAuth()) return false;
    const user = this.getUser();
    if (!user || user.role !== role) {
      UI.showToast('You do not have permission to view this page.', 'error');
      window.location.href = 'index.html';
      return false;
    }
    return true;
  }
}
