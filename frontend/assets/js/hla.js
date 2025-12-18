import { getPacientes, guardarHlaAPI } from "./api.js";
import { showAlert } from "./alerts.js";
import { showLoader, hideLoader } from "./loader.js";

// Guarda el paciente encontrado
let pacienteEncontrado = null;

// Referencias a elementos del DOM
let formDisplayNombre, formDisplayDni, formDisplayMuestra, hiddenIdInput, nroMuestraInput, form;

/**
 * Resetea la información del paciente mostrado en la interfaz.
 */
function limpiarPacienteEncontrado() {
    pacienteEncontrado = null;
    // Limpia los campos dentro del formulario
    if (formDisplayNombre) formDisplayNombre.textContent = "[Ninguno]";
    if (formDisplayDni) formDisplayDni.textContent = "-";
    if (formDisplayMuestra) formDisplayMuestra.textContent = "-";
    if (hiddenIdInput) hiddenIdInput.value = "";
    if (nroMuestraInput) nroMuestraInput.value = ""; // Limpia el input oculto
}

/**
 * Busca un paciente por DNI o Nro. de Muestra y actualiza la UI.
 */
async function buscarPaciente() {
    const dni = document.getElementById("searchDniHLA").value.trim();
    const muestra = document.getElementById("searchMuestraHLA").value.trim();

    if (!dni && !muestra) {
        showAlert("Debe ingresar un DNI o un Nro. de Muestra para buscar.", "warning", 3000, "formAlertsHLA");
        return;
    }

    showLoader();
    try {
        const pacientes = await getPacientes();
        hideLoader();

        const paciente = pacientes.find(p =>
            (dni && p.dni === dni) || (muestra && p.numeroMuestra === muestra)
        );

        if (paciente) {
            pacienteEncontrado = paciente;

            // Actualiza los campos dentro del formulario
            formDisplayNombre.textContent = `${paciente.nombre} ${paciente.apellido}`;
            formDisplayDni.textContent = `${paciente.dni || "-"}`;
            formDisplayMuestra.textContent = `${paciente.numeroMuestra || "No asignado"}`;
            hiddenIdInput.value = paciente.idPaciente;
            nroMuestraInput.value = paciente.numeroMuestra || ""; // Llena input oculto

            // Dispara evento para validación
            nroMuestraInput.dispatchEvent(new Event('input'));

            showAlert("Paciente encontrado.", "success", 2000, "formAlertsHLA");

        } else {
            limpiarPacienteEncontrado();
            showAlert("Paciente no encontrado.", "danger", 3000, "formAlertsHLA");
        }
    } catch (error) {
        hideLoader();
        console.error('Error en buscarPaciente:', error);
        showAlert("Error al cargar la lista de pacientes.", "danger", 4000, "formAlertsHLA");
        limpiarPacienteEncontrado();
    }
}

/**
 * Configura los listeners y obtiene referencias del DOM al cargar el módulo.
 */
export function initHlaModule() {
    form = document.getElementById("formHLA");
    if (!form) return;

    // Obtiene referencias del DOM (usando IDs de HLA)
    formDisplayNombre = document.getElementById("formDisplayNombreHLA");
    formDisplayDni = document.getElementById("formDisplayDniHLA");
    formDisplayMuestra = document.getElementById("formDisplayMuestraHLA");
    hiddenIdInput = document.getElementById("hiddenPacienteIdHLA");
    nroMuestraInput = document.getElementById("numeroMuestraHLA"); // Input oculto

    // Asigna listener al botón de búsqueda
    document.getElementById("btnBuscarPacienteHLA").addEventListener("click", buscarPaciente);

    // Asigna listener al envío del formulario
    form.addEventListener("submit", async (e) => {
        e.preventDefault();

        const idPaciente = hiddenIdInput.value;
        if (!idPaciente) {
            showAlert("Debe buscar y seleccionar un paciente primero.", "warning", 3000, "formAlertsHLA");
            form.classList.add("was-validated");
            return;
        }

        if (!form.checkValidity()) {
            showAlert("Por favor, complete todos los campos obligatorios.", "warning", 3000, "formAlertsHLA");
            form.classList.add("was-validated");
            return;
        }

        // Prepara los datos para enviar
        const hlaData = {
            fechaRegistro: form.fechaRegistro.value,
            numeroMuestra: form.numeroMuestraHLA.value, // Valor del input oculto
            grupoSanguineo: form.grupoSanguineo.value,
            locusA01: form.locusA01.value || null, // Usar null si está vacío
            locusA02: form.locusA02.value || null,
            locusB01: form.locusB01.value || null,
            locusB02: form.locusB02.value || null,
            locusC01: form.locusC01.value || null,
            locusC02: form.locusC02.value || null,
            locusDR01: form.locusDR01.value || null,
            locusDR02: form.locusDR02.value || null,
            locusDQA01: form.locusDQA01.value || null,
            locusDQA02: form.locusDQA02.value || null,
            locusDQB01: form.locusDQB01.value || null,
            locusDQB02: form.locusDQB02.value || null,
            locusDPA01: form.locusDPA01.value || null,
            locusDPA02: form.locusDPA02.value || null,
            locusDPB01: form.locusDPB01.value || null,
            locusDPB02: form.locusDPB02.value || null
        };

        try {
            showLoader();
            await guardarHlaAPI(idPaciente, hlaData); // Llama a la API correcta
            hideLoader();
            showAlert("Estudio HLA guardado correctamente.", "success", 3000, "formAlertsHLA");

            // Limpia formulario y estado
            form.reset();
            form.classList.remove("was-validated");
            limpiarPacienteEncontrado();

        } catch (err) {
            hideLoader();
            console.error('Error al guardar HLA:', err);
            showAlert(err.message || "Error al guardar el estudio HLA.", "danger", 4000, "formAlertsHLA");
        }
    });
}