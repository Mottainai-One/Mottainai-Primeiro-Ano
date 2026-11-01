// Os radios do layout recebido controlam abertura e fechamento do modal de login.
document.addEventListener("keydown", (event) => {
  if (event.key === "Escape") document.getElementById("fechar").checked = true;
});
