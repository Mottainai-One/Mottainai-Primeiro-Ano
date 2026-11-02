/**
 * sistema-accordeon.js
 * Cards da seção "O sistema completo" — accordion no mobile (≤480px)
 * No tablet/desktop o card-corpo fica sempre visível via CSS.
 */
(function () {
  'use strict';

  var MOBILE_BP = 480;

  function isMobile() {
    return window.innerWidth <= MOBILE_BP;
  }

  var cards = document.querySelectorAll('.card-accordeon');
  if (!cards.length) return;

  cards.forEach(function (card) {
    var btn = card.querySelector('.card-topo');
    if (!btn) return;

    btn.addEventListener('click', function () {
      if (!isMobile()) return; // accordion only on mobile

      var isOpen = btn.getAttribute('aria-expanded') === 'true';

      // Close all others
      cards.forEach(function (c) {
        var b = c.querySelector('.card-topo');
        if (b && b !== btn) {
          b.setAttribute('aria-expanded', 'false');
          c.classList.remove('aberto');
        }
      });

      // Toggle clicked
      btn.setAttribute('aria-expanded', isOpen ? 'false' : 'true');
      card.classList.toggle('aberto', !isOpen);
    });
  });
})();
