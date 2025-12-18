import { showAlert } from "./alerts.js";
import { showLoader, hideLoader } from "./loader.js";

export function initAnticuerposModule() {
  const form = document.getElementById("formAnticuerpos");
  if (!form) return;

  form.addEventListener("submit", async (e) => {
    e.preventDefault();
    if (!form.checkValidity()) {
      form.classList.add("was-validated");
      showAlert("Por favor, complete todos los campos correctamente", "warning", 3000, "formAlerts");
      return;
    }

    const anticuerpos = {
      fecha: form.fechaAnticuerpos.value,
      numero: form.numeroAnticuerpos.value,
      antiHlaI: form.antiHlaI.value,
      antiHlaII: form.antiHlaII.value,
      antiMica: form.antiMica.value,
      noConfirmados: form.anticuerposNoConfirmados.value,
    };

    try {
      showLoader();
      await guardarAnticuerpos(anticuerpos);
      hideLoader();
      showAlert("Anticuerpos guardados correctamente", "success", 3000, "formAlerts");
      form.reset();
      form.classList.remove("was-validated");
    } catch (err) {
      hideLoader();
      showAlert("Error al guardar anticuerpos", "danger", 3000, "formAlerts");
    }
  });
}

// Mock
async function guardarAnticuerpos(data) {
  return Promise.resolve({ success: true });
}
