/* BorrowBox Dynamic Navbar Component */

document.addEventListener('DOMContentLoaded', () => {
  renderNavbar();
});

async function renderNavbar() {
  const headerContainer = document.getElementById('app-header');
  if (!headerContainer) return;

  const user = Auth.getUser();
  const isAuth = Auth.isAuthenticated();

  let unreadCount = 0;
  if (isAuth) {
    try {
      unreadCount = await ApiClient.get('/notifications/unread-count');
    } catch (e) {}
  }

  const userMenuHtml = isAuth ? `
    <div style="position: relative;">
      <button class="user-profile-menu" onclick="toggleUserDropdown(event)">
        <img src="${user.avatarUrl || 'https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=200&q=80'}" class="avatar-img" alt="${user.fullName}">
        <span style="font-weight: 600; font-size: 0.9rem;">${user.fullName.split(' ')[0]}</span>
        <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="6 9 12 15 18 9"></polyline></svg>
      </button>

      <div class="dropdown-menu" id="user-dropdown">
        <div style="padding: 0.6rem 0.85rem; border-bottom: 1px solid var(--border-color); margin-bottom: 0.25rem;">
          <div style="font-weight: 700; font-size: 0.95rem; color: var(--text-primary);">${user.fullName}</div>
          <div style="font-size: 0.78rem; color: var(--text-secondary);">${user.email}</div>
          <div style="margin-top: 0.35rem;"><span class="badge ${user.role === 'ROLE_ADMIN' ? 'badge-completed' : (user.role === 'ROLE_OWNER' ? 'badge-active' : 'badge-approved')}">${user.role.replace('ROLE_', '')}</span></div>
        </div>
        
        ${user.role === 'ROLE_ADMIN' ? `
          <a href="admin-dashboard.html" class="dropdown-item">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M12 2L2 7l10 5 10-5-10-5z"></path><path d="M2 17l10 5 10-5"></path><path d="M2 12l10 5 10-5"></path></svg>
            <span>Admin Dashboard</span>
          </a>
        ` : ''}

        <a href="renter-dashboard.html" class="dropdown-item">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M21 8-2 2 2 14 2 2 8z"></path><path d="M6 2v20"></path></svg>
          <span>Renter Dashboard</span>
        </a>

        <a href="owner-dashboard.html" class="dropdown-item">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M20 7h-7L10 4H4c-1.1 0-2 .9-2 2v12c0 1.1.9 2 2 2h16c1.1 0 2-.9 2-2V9c0-1.1-.9-2-2-2z"></path></svg>
          <span>Owner Dashboard</span>
        </a>

        <a href="wishlist.html" class="dropdown-item">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z"></path></svg>
          <span>Wishlist</span>
        </a>

        <a href="notifications.html" class="dropdown-item">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9"></path><path d="M13.73 21a2 2 0 0 1-3.46 0"></path></svg>
          <span>Notifications ${unreadCount > 0 ? `<span class="badge badge-pending" style="padding: 0.1rem 0.4rem; font-size: 0.7rem;">${unreadCount}</span>` : ''}</span>
        </a>

        <a href="profile.html" class="dropdown-item">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path><circle cx="12" cy="7" r="4"></circle></svg>
          <span>Profile Settings</span>
        </a>

        <div style="border-top: 1px solid var(--border-color); margin-top: 0.25rem; padding-top: 0.25rem;">
          <a href="#" class="dropdown-item" style="color: var(--danger);" onclick="Auth.logout()">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"></path><polyline points="16 17 21 12 16 7"></polyline><line x1="21" y1="12" x2="9" y2="12"></line></svg>
            <span>Log Out</span>
          </a>
        </div>
      </div>
    </div>
  ` : `
    <a href="login.html" class="btn btn-secondary btn-sm">Log In</a>
    <a href="register.html" class="btn btn-primary btn-sm">Sign Up</a>
  `;

  headerContainer.innerHTML = `
    <nav class="navbar">
      <div class="container navbar-inner">
        <a href="index.html" class="brand-logo">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round"><path d="M21 16V8a2 2 0 0 0-1-1.73l-7-4a2 2 0 0 0-2 0l-7 4A2 2 0 0 0 3 8v8a2 2 0 0 0 1 1.73l7 4a2 2 0 0 0 2 0l7-4A2 2 0 0 0 21 16z"></path><polyline points="3.27 6.96 12 12.01 20.73 6.96"></polyline><line x1="12" y1="22.08" x2="12" y2="12"></line></svg>
          <span>Borrow<span style="color: var(--primary);">Box</span></span>
        </a>

        <ul class="nav-menu">
          <li><a href="explore.html" class="nav-link">Explore</a></li>
          <li><a href="explore.html#categories" class="nav-link">Categories</a></li>
          <li><a href="index.html#how-it-works" class="nav-link">How It Works</a></li>
          <li><a href="add-equipment.html" class="nav-link" style="color: var(--primary); font-weight: 600;">+ List Equipment</a></li>
        </ul>

        <div class="nav-actions">
          ${userMenuHtml}
        </div>
      </div>
    </nav>
  `;
}

function toggleUserDropdown(e) {
  e.stopPropagation();
  const dropdown = document.getElementById('user-dropdown');
  if (dropdown) {
    dropdown.classList.toggle('show');
  }
}

document.addEventListener('click', () => {
  const dropdown = document.getElementById('user-dropdown');
  if (dropdown && dropdown.classList.contains('show')) {
    dropdown.classList.remove('show');
  }
});
