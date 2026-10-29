const secao = document.getElementById("escolha-plano");
const toggle = document.getElementById("toggle-plano");
const retangulo = document.getElementById("retangulo");
const opcaoMensal = document.getElementById("mensal");
const opcaoAnual = document.getElementById("anual");

const PRECOS = {
  mensal: { essentials: 1200, professional: 3500 },
  anual: { essentials: 10000, professional: 28000 },
};

const DURACAO_MS = 600;
const elPrecos = {
  essentials: document.querySelector(
    ".card-plano:not(#principal):first-child .preco strong",
  ),
  professional: document.querySelector("#principal .preco strong"),
};

let modo = "mensal";
const animacoesPreco = new Map();
function prepararToggle() {
  toggle.insertBefore(opcaoMensal, retangulo);
  retangulo.classList.add("deslizante");
  posicionarRetangulo(false);
}

function posicionarRetangulo(animar = true) {
  const alvo = modo === "mensal" ? opcaoMensal : opcaoAnual;
  retangulo.style.transition = animar ? "" : "none";
  retangulo.style.width = `${alvo.offsetWidth}px`;
  retangulo.style.height = `${alvo.offsetHeight}px`;
  retangulo.style.transform = `translateX(${alvo.offsetLeft}px)`;
  if (!animar)
    (retangulo.getBoundingClientRect(), (retangulo.style.transition = ""));
}

/* ---------- Contagem dos preços ---------- */
const formatarBRL = (n) => `R$ ${Math.round(n).toLocaleString("pt-BR")}`;
const easeOutCubic = (t) => 1 - Math.pow(1 - t, 3);

function animarPreco(el, de, para) {
  cancelAnimationFrame(animacoesPreco.get(el));
  const inicio = performance.now();

  function frame(agora) {
    const t = Math.min((agora - inicio) / DURACAO_MS, 1);
    el.textContent = formatarBRL(de + (para - de) * easeOutCubic(t));
    if (t < 1) animacoesPreco.set(el, requestAnimationFrame(frame));
  }
  animacoesPreco.set(el, requestAnimationFrame(frame));
}

function valorAtual(el) {
  return Number(el.textContent.replace(/\D/g, "")) || 0;
}

function definirModo(novoModo) {
  if (novoModo === modo) return;
  modo = novoModo;

  secao.classList.toggle("anual", modo === "anual");
  opcaoMensal.classList.toggle("ativa", modo === "mensal");
  opcaoAnual.classList.toggle("ativa", modo === "anual");
  posicionarRetangulo();

  for (const plano in elPrecos) {
    const el = elPrecos[plano];
    animarPreco(el, valorAtual(el), PRECOS[modo][plano]);
  }
}

opcaoMensal.addEventListener("click", () => definirModo("mensal"));
opcaoAnual.addEventListener("click", () => definirModo("anual"));
window.addEventListener("resize", () => posicionarRetangulo(false));

prepararToggle();
opcaoMensal.classList.add("ativa");
document.fonts?.ready.then(() => posicionarRetangulo(false));

/* ---------- Carrossel de planos (tablet e celular) ---------- */
const cards = document.querySelectorAll(".container .card-plano");
const carrosselContainer = document.querySelector(".carrossel-planos");
const glowFundo = document.getElementById("glow-fundo");
const cardPrincipal = document.getElementById("principal");
let atual = 1; // começa no plano Professional

function mostrarPlano(indice) {
  atual = (indice + cards.length) % cards.length;
  cards.forEach((card, i) => card.classList.toggle("visivel", i === atual));

  if (window.innerWidth <= 1024 && glowFundo) {
    const isPrincipal = cards[atual].id === "principal";
    glowFundo.style.opacity = isPrincipal ? "1" : "0";
    glowFundo.style.transform = "translate(-50%, -50%)";
  }
}

document
  .getElementById("seta-anterior")
  .addEventListener("click", () => mostrarPlano(atual - 1));
document
  .getElementById("seta-proximo")
  .addEventListener("click", () => mostrarPlano(atual + 1));

mostrarPlano(atual);

/* ---------- Efeito de hover / destaque nos cards (Desktop) ---------- */

function posicionarGlow(card, animar = true) {
  if (!glowFundo || !card || window.innerWidth <= 1024) return;
  const carrosselRect = carrosselContainer.getBoundingClientRect();
  const cardRect = card.getBoundingClientRect();

  const x = cardRect.left + cardRect.width / 2 - carrosselRect.left;
  const y = cardRect.top + cardRect.height / 2 - carrosselRect.top;

  glowFundo.style.transition = animar
    ? "transform 0.45s cubic-bezier(0.4, 0, 0.2, 1), opacity 0.45s ease"
    : "none";
  glowFundo.style.transform = `translate(${x - 450}px, ${y - 450}px)`;
  glowFundo.style.opacity = "1";
}

cards.forEach((card) => {
  card.addEventListener("mouseenter", () => {
    if (window.innerWidth > 1024) {
      cards.forEach((c) => c.classList.remove("destacado"));
      card.classList.add("destacado");
      requestAnimationFrame(() => posicionarGlow(card, true));
    }
  });
});

if (carrosselContainer) {
  carrosselContainer.addEventListener("mouseleave", () => {
    if (window.innerWidth > 1024) {
      cards.forEach((c) => c.classList.remove("destacado"));
      requestAnimationFrame(() => posicionarGlow(cardPrincipal, true));
    }
  });
}

function atualizarGlowInicial() {
  if (cardPrincipal && window.innerWidth > 1024) {
    posicionarGlow(cardPrincipal, false);
  }
}

window.addEventListener("resize", () => {
  posicionarRetangulo(false);
  const cardAtivo =
    document.querySelector(".card-plano.destacado") || cardPrincipal;
  posicionarGlow(cardAtivo, false);
});

// Inicialização
document.fonts?.ready.then(() => {
  posicionarRetangulo(false);
  atualizarGlowInicial();
});
setTimeout(atualizarGlowInicial, 100);
    