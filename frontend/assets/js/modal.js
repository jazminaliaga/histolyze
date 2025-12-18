let genericModal;

export function initModal() {
  const modalEl = document.getElementById("genericModal");
  if (modalEl) genericModal = new bootstrap.Modal(modalEl);

  document.getElementById("modalConfirmBtn")?.addEventListener("click", () => hideModal());
}

export function showModal(title = "Título", body = "Contenido") {
  if (!genericModal) return;

  const titleEl = document.getElementById("genericModalLabel") || document.getElementById("genericModalTitle");
  if (titleEl) titleEl.textContent = title;

  const bodyEl = document.querySelector("#genericModal .modal-body") || document.getElementById("genericModalBody");
  if (bodyEl) bodyEl.innerHTML = body;

  genericModal.show();
}

export function hideModal() {
  genericModal?.hide();
}
