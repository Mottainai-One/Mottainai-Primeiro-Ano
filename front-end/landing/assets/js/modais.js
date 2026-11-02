// O HTML é uma demonstração visual: a autenticação requer o servidor Java.
(() => {
  const close = document.getElementById('fechar');
  const login = document.getElementById('login');
  const email = document.querySelector('#login-form input[type="email"]');
  const open = () => { login.checked = true; email.focus({ preventScroll:true }); };
  document.querySelectorAll('a[href$="#login"]').forEach(link => link.addEventListener('click', event => { event.preventDefault(); open(); }));
  const fromHash = () => { if (location.hash === '#login') { open(); history.replaceState(null, '', location.pathname + location.search); } };
  window.addEventListener('hashchange', fromHash);
  fromHash();
  document.addEventListener('keydown', event => { if (event.key === 'Escape') close.checked = true; });
  document.getElementById('login-form').addEventListener('submit', event => {
    event.preventDefault();
    const status = document.getElementById('login-status');
    status.hidden = false;
    status.textContent = 'Esta versão HTML não está conectada ao servidor de autenticação.';
  });
})();
