export function showAlert(message, type = "success", duration = 3000, containerId = "alerts-container") {
  const container = document.getElementById(containerId);
  if (!container) return;

  const alert = document.createElement("div");
  alert.className = `alert alert-${type} alert-dismissible fade show`;
  alert.role = "alert";
  alert.innerHTML = `
    ${message}
    <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Cerrar"></button>
  `;

  container.appendChild(alert);
  setTimeout(() => bootstrap.Alert.getOrCreateInstance(alert).close(), duration);
}
