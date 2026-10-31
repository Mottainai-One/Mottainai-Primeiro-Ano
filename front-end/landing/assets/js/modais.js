// Os radios do layout recebido controlam abertura, fechamento e alternância dos modais.
document.addEventListener("keydown", (event) => {
  if (event.key === "Escape") document.getElementById("fechar").checked = true;
});

// O destino externo ainda não foi fornecido: Enter nunca deve enviar Cadastro à Home ou ao backend.
document.getElementById("cadastro-form").addEventListener("submit", (event) => event.preventDefault());

// Links da área administrativa podem abrir diretamente o cadastro da Home.
function abrirModalPeloEndereco() {
  const destino = window.location.hash;
  if (destino !== "#cadastro" && destino !== "#login") return;
  const radio = document.getElementById(destino.slice(1));
  if (!radio) return;
  radio.checked = true;
  radio.closest(".modal").querySelector("input:not([type=radio]):not([hidden])")?.focus({ preventScroll:true });
  // Consome o atalho para que fechar o modal não deixe um destino antigo na URL.
  history.replaceState(history.state, "", window.location.pathname + window.location.search);
}
abrirModalPeloEndereco();
window.addEventListener("hashchange", abrirModalPeloEndereco);
