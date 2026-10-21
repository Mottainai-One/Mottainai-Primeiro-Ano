(function marcarItemAtivo() {
  const paginaAtual = document.body.dataset.pagina;
  if (!paginaAtual) return;
  const itemAtivo = document.querySelector(
    `.sidebar-item[data-pagina="${paginaAtual}"]`
  );
  if (itemAtivo) itemAtivo.classList.add("ativo");
})();

const overlays = document.querySelectorAll(".modal-overlay");
function abrirModal(id) {
  const overlay = document.getElementById(id);
  if (!overlay) return;
  overlay.classList.add("aberto");
  document.body.style.overflow = "hidden";
}

function fecharModal(overlay) {
  if (!overlay) return;
  overlay.classList.remove("aberto");
  if (document.querySelectorAll(".modal-overlay.aberto").length === 0) {
    document.body.style.overflow = "";
  }
}

document.querySelectorAll("[data-abrir-modal]").forEach((botao) => {
  botao.addEventListener("click", () => {
    abrirModal(botao.dataset.abrirModal);
  });
});

document.querySelectorAll("[data-fechar-modal]").forEach((botao) => {
  botao.addEventListener("click", () => {
    fecharModal(botao.closest(".modal-overlay"));
  });
});

overlays.forEach((overlay) => {
  overlay.addEventListener("click", (e) => {
    if (e.target === overlay) fecharModal(overlay);
  });
});

document.addEventListener("keydown", (e) => {
  if (e.key === "Escape") {
    const aberto = document.querySelector(".modal-overlay.aberto");
    if (aberto) fecharModal(aberto);
  }
});

function mascaraCNPJ(valor) {
  return valor
    .replace(/\D/g, "")                            
    .slice(0, 14)                                  
    .replace(/^(\d{2})(\d)/, "$1.$2")              
    .replace(/^(\d{2})\.(\d{3})(\d)/, "$1.$2.$3")  
    .replace(/\.(\d{3})(\d)/, ".$1/$2")            
    .replace(/(\d{4})(\d)/, "$1-$2");              
}

function mascaraTelefone(valor) {
  return valor
    .replace(/\D/g, "")                           
    .slice(0, 11)                                 
    .replace(/^(\d{2})(\d)/, "($1) $2")            
    .replace(/(\d{5})(\d)/, "$1-$2");              
}

document.addEventListener("input", (e) => {
  const input = e.target;
  if (!input.dataset || !input.dataset.mascara) return;
  if (input.dataset.mascara === "cnpj") {
    input.value = mascaraCNPJ(input.value);
  } else if (input.dataset.mascara === "telefone") {
    input.value = mascaraTelefone(input.value);
  }
});

function marcarErro(campo, mensagem) {
  campo.classList.add("com-erro");
  const span = campo.querySelector(".campo-erro");
  if (span) span.textContent = mensagem;
}

function limparErro(campo) {
  campo.classList.remove("com-erro");
  const span = campo.querySelector(".campo-erro");
  if (span) span.textContent = "";
}

function validarObrigatorio(campo) {
  const input = campo.querySelector("input, select");
  if (!input) return true;
  if (!input.value.trim()) {
    marcarErro(campo, "Este campo é obrigatório.");
    return false;
  }
  limparErro(campo);
  return true;
}

function validarEmail(campo) {
  const input = campo.querySelector("input");
  if (!input) return true;
  if (!input.value.trim()) return true;
  const regex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  if (!regex.test(input.value.trim())) {
    marcarErro(campo, "Informe um e-mail válido.");
    return false;
  }
  limparErro(campo);
  return true;
}

function validarCNPJ(campo) {
  const input = campo.querySelector("input");
  if (!input) return true;
  if (!input.value.trim()) return true;
  const digitos = input.value.replace(/\D/g, "");
  if (digitos.length !== 14) {
    marcarErro(campo, "CNPJ deve ter 14 dígitos.");
    return false;
  }
  limparErro(campo);
  return true;
}

function validarTelefone(campo) {
  const input = campo.querySelector("input");
  if (!input) return true;
  if (!input.value.trim()) return true;
  const digitos = input.value.replace(/\D/g, "");
  if (digitos.length !== 10 && digitos.length !== 11) {
    marcarErro(campo, "Telefone deve ter 10 ou 11 dígitos.");
    return false;
  }
  limparErro(campo);
  return true;
}

function validarNumeroDecimal(campo) {
  const input = campo.querySelector("input");
  if (!input) return true;
  if (!input.value.trim()) return true;
  const numero = Number(input.value.replace(",", "."));
  if (Number.isNaN(numero)) {
    marcarErro(campo, "Informe um número válido.");
    return false;
  }
  limparErro(campo);
  return true;
}

function validarCampo(campo) {
  const input = campo.querySelector("input, select");
  if (!input) return true;
  const name = input.name;
  const temAsterisco = campo.querySelector(".obrigatorio");
  let ok = true;
  if (temAsterisco) ok = validarObrigatorio(campo) && ok;
  if (name === "email") ok = validarEmail(campo) && ok;
  if (name === "cnpj") ok = validarCNPJ(campo) && ok;
  if (name === "phone") ok = validarTelefone(campo) && ok;
  if (name === "latitude" || name === "longitude")
    ok = validarNumeroDecimal(campo) && ok;
  return ok;
}

document.querySelectorAll("[data-formulario]").forEach((form) => {
  form.addEventListener("submit", (e) => {
    e.preventDefault();
    if (form.dataset.formulario === "ir-pagina") return;
    const campos = form.querySelectorAll(".campo");
    let tudoOk = true;
    campos.forEach((campo) => {
      const ok = validarCampo(campo);
      if (!ok) tudoOk = false;
    });
    if (!tudoOk) {
      const primeiroErro = form.querySelector(
        ".campo.com-erro input, .campo.com-erro select"
      );
      if (primeiroErro) primeiroErro.focus();
      return;
    }
    const overlay = form.closest(".modal-overlay");
    if (overlay) fecharModal(overlay);
    form.reset();
  });
});

document.addEventListener(
  "blur",
  (e) => {
    const input = e.target;
    if (!input.matches || !input.matches("input, select")) return;
    const campo = input.closest(".campo");
    if (!campo) return;
    validarCampo(campo);
  },
  true
);

const formIrPagina = document.querySelector('[data-formulario="ir-pagina"]');
if (formIrPagina) {
  formIrPagina.addEventListener("submit", (e) => {
    e.preventDefault();
    const campo = formIrPagina.querySelector(".campo");
    const input = formIrPagina.querySelector("input");
    const valor = parseInt(input.value, 10);
    if (isNaN(valor) || valor < 1 || valor > 10) {
      marcarErro(campo, "Informe um número entre 1 e 10.");
      input.focus();
      return;
    }
    limparErro(campo);
    const overlay = formIrPagina.closest(".modal-overlay");
    if (overlay) fecharModal(overlay);
    formIrPagina.reset();
  });
}