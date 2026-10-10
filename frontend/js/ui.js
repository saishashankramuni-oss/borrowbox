/* BorrowBox UI Utility Library */

class UI {
  static formatCurrency(amount) {
    if (amount === undefined || amount === null) return '₹0';
    return new Intl.NumberFormat('en-IN', {
      style: 'currency',
      currency: 'INR',
      maximumFractionDigits: 0
    }).format(amount);
  }

  static formatDate(dateStr) {
    if (!dateStr) return '';
    const date = new Date(dateStr);
    return new Intl.DateTimeFormat('en-IN', {
      day: 'numeric',
      month: 'short',
      year: 'numeric'
    }).format(date);
  }

  static showToast(message, type = 'info') {
    let container = document.getElementById('toast-container');
    if (!container) {
      container = document.createElement('div');
      container.id = 'toast-container';
      document.body.appendChild(container);
    }

    const toast = document.createElement('div');
    toast.className = `toast toast-${type}`;

    let iconSvg = '';
    if (type === 'success') {
      iconSvg = `<svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"></path><polyline points="22 4 12 14.01 9 11.01"></polyline></svg>`;
    } else if (type === 'error') {
      iconSvg = `<svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"></circle><line x1="15" y1="9" x2="9" y2="15"></line><line x1="9" y1="9" x2="15" y2="15"></line></svg>`;
    } else {
      iconSvg = `<svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"></circle><line x1="12" y1="16" x2="12" y2="12"></line><line x1="12" y1="8" x2="12.01" y2="8"></line></svg>`;
    }

    toast.innerHTML = `
      ${iconSvg}
      <span style="font-weight: 500;">${message}</span>
    `;

    container.appendChild(toast);

    setTimeout(() => {
      toast.style.opacity = '0';
      toast.style.transform = 'translateX(100%)';
      toast.style.transition = 'all 0.3s ease';
      setTimeout(() => toast.remove(), 300);
    }, 4000);
  }

  static renderEquipmentCard(eq, isSaved = false) {
    const rawImg = eq.primaryImageUrl || (eq.images && eq.images.length > 0 ? eq.images[0].filePath : 'https://images.unsplash.com/photo-1516035069371-29a1b244cc32?auto=format&fit=crop&w=800&q=80');
    const primaryImg = typeof ApiClient !== 'undefined' && ApiClient.resolveImageUrl ? ApiClient.resolveImageUrl(rawImg) : rawImg;
    const categoryName = eq.category ? eq.category.name : 'Equipment';
    const rating = eq.ratingAverage ? Number(eq.ratingAverage).toFixed(1) : 'New';

    return `
      <div class="equipment-card">
        <div class="card-image-wrap">
          <img src="${primaryImg}" alt="${eq.name}" class="card-image" loading="lazy" onerror="this.src='https://images.unsplash.com/photo-1516035069371-29a1b244cc32?auto=format&fit=crop&w=800&q=80'">
          <span class="card-category-tag">${categoryName}</span>
          <button class="wishlist-btn ${isSaved ? 'active' : ''}" onclick="toggleWishlist(${eq.id}, this)" title="Save to wishlist">
            <svg width="18" height="18" viewBox="0 0 24 24" fill="${isSaved ? '#EF4444' : 'none'}" stroke="currentColor" stroke-width="2"><path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z"></path></svg>
          </button>
        </div>
        <div class="card-content">
          <a href="equipment-details.html?id=${eq.id}" class="card-title">${eq.name}</a>
          <div class="card-location">
            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z"></path><circle cx="12" cy="10" r="3"></circle></svg>
            <span>${eq.location}</span>
          </div>
          <div class="card-footer-row">
            <div class="price-display">
              <span class="price-amount">${this.formatCurrency(eq.dailyPrice)}</span>
              <span class="price-unit">per day</span>
            </div>
            <div class="rating-badge">
              <svg width="14" height="14" viewBox="0 0 24 24"><polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"></polygon></svg>
              <span>${rating}</span>
            </div>
          </div>
        </div>
      </div>
    `;
  }

  static renderStatusBadge(status) {
    const statusMap = {
      'PENDING': 'badge-pending',
      'APPROVED': 'badge-approved',
      'ACTIVE': 'badge-active',
      'COMPLETED': 'badge-completed',
      'REJECTED': 'badge-rejected',
      'CANCELLED': 'badge-cancelled',
      'READY_FOR_PICKUP': 'badge-approved',
      'RETURN_REQUESTED': 'badge-pending'
    };
    const cls = statusMap[status] || 'badge-pending';
    return `<span class="badge ${cls}">${status.replace(/_/g, ' ')}</span>`;
  }
}

async function toggleWishlist(equipmentId, btnElement) {
  if (!Auth.isAuthenticated()) {
    UI.showToast('Please login to save equipment to your wishlist.', 'warning');
    window.location.href = 'login.html';
    return;
  }
  try {
    const saved = await ApiClient.post(`/wishlist/${equipmentId}`);
    btnElement.classList.toggle('active', saved);
    const svg = btnElement.querySelector('svg');
    if (svg) svg.setAttribute('fill', saved ? '#EF4444' : 'none');
    UI.showToast(saved ? 'Added to wishlist!' : 'Removed from wishlist', 'success');
  } catch (e) {
    UI.showToast(e.message || 'Failed to update wishlist', 'error');
  }
}
