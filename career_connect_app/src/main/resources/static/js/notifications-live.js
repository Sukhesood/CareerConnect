/* Shared by header-seeker and header-recruiter from fragments.html. */
(() => {
  const badge = document.querySelector('.notification-badge[data-count-url]');
  if (!badge) return;  // Public or unauthenticated page.
  const countUrl = badge.dataset.countUrl;
  const streamUrl = badge.dataset.streamUrl;
  const notificationsUrl = badge.dataset.notificationsUrl;

  async function refreshCount() {
    try {
      const response = await fetch(countUrl, {credentials: 'same-origin', cache: 'no-store'});
      if (!response.ok) return;
      const payload = await response.json();
      const count = Number(payload.count) || 0;
      badge.textContent = count > 99 ? '99+' : String(count);
      badge.hidden = count === 0;
    } catch (error) {
      // A temporary network error should not hide the rest of the navbar.
      console.debug('Notification count unavailable', error);
    }
  }

  function announceNewNotification() {
    // Updates the badge immediately and shows a small accessible notification.
    refreshCount();
    const old = document.querySelector('.cc-notification-toast');
    if (old) old.remove();
    const toast = document.createElement('a');
    toast.className = 'cc-notification-toast';
    toast.href = notificationsUrl;
    toast.textContent = 'New CareerConnect notification — view';
    toast.setAttribute('role', 'status');
    document.body.appendChild(toast);
    setTimeout(() => toast.remove(), 6000);
  }

  refreshCount();
  // Recover missed messages after a reconnect, tab sleep, or temporary outage.
  document.addEventListener('visibilitychange', () => {
    if (!document.hidden) refreshCount();
  });
  setInterval(refreshCount, 45000);

  if (window.EventSource) {
    const source = new EventSource(streamUrl);
    source.addEventListener('notification', () => announceNewNotification());
    source.addEventListener('connected', refreshCount);
  }
})();