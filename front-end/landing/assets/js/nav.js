/**
 * nav.js — Mobile/Tablet hamburger menu toggle
 * Used by: home.html, sobre-nos.html, planos.html
 * Breakpoint: ≤1024px (set via CSS to show hamburger)
 */
(function () {
  'use strict';

  var btn = document.getElementById('btn-hamburger');
  var nav = document.getElementById('nav-principal');

  if (!btn || !nav) return;

  function openMenu() {
    btn.setAttribute('aria-expanded', 'true');
    btn.setAttribute('aria-label', 'Fechar menu');
    btn.classList.add('aberto');
    nav.classList.add('aberto');
  }

  function closeMenu() {
    btn.setAttribute('aria-expanded', 'false');
    btn.setAttribute('aria-label', 'Abrir menu');
    btn.classList.remove('aberto');
    nav.classList.remove('aberto');
  }

  btn.addEventListener('click', function () {
    if (nav.classList.contains('aberto')) {
      closeMenu();
    } else {
      openMenu();
    }
  });

  // Close when any nav link is clicked
  nav.querySelectorAll('a').forEach(function (link) {
    link.addEventListener('click', closeMenu);
  });

  // Close on Escape key
  document.addEventListener('keydown', function (e) {
    if (e.key === 'Escape' && nav.classList.contains('aberto')) {
      closeMenu();
      btn.focus();
    }
  });

  // Close when clicking outside the header
  document.addEventListener('click', function (e) {
    var header = btn.closest('header');
    if (header && !header.contains(e.target) && nav.classList.contains('aberto')) {
      closeMenu();
    }
  });
})();
