import { getPacientes, guardarCrossmatchAPI } from "./api.js";
import { showAlert } from "./alerts.js";
import { showLoader, hideLoader } from "./loader.js";

// Guarda el paciente encontrado en la búsqueda
let pacienteEncontrado = null;

// Referencias a elementos del DOM
let hiddenIdInput, nroMuestraInput, form;
let formDisplayNombre, formDisplayDni, formDisplayMuestra;

/**
 * Resetea la información del paciente mostrado en la interfaz.
 */
function limpiarPacienteEncontrado() {
    pacienteEncontrado = null;
    if (hiddenIdInput) hiddenIdInput.value = "";

    // Limpia Sección 2 (datos dentro del formulario)
    if (formDisplayNombre) formDisplayNombre.textContent = "[Busque y seleccione un paciente arriba]";
    if (formDisplayDni) formDisplayDni.textContent = "-";
    if (formDisplayMuestra) formDisplayMuestra.textContent = "-";
    if (nroMuestraInput) nroMuestraInput.value = ""; // Limpia el input oculto también
}

/**
 * Busca un paciente por DNI o Nro. de Muestra y actualiza la UI.
 */
async function buscarPaciente() {
    const dni = document.getElementById("searchDniCM").value.trim();
    const muestra = document.getElementById("searchMuestraCM").value.trim();

    if (!dni && !muestra) {
        showAlert("Debe ingresar un DNI o un Nro. de Muestra para buscar.", "warning", 3000, "formAlertsCrossmatch");
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

            hiddenIdInput.value = paciente.idPaciente;
            nroMuestraInput.value = paciente.numeroMuestra || ""; // Llena el input oculto

            // Actualiza Sección 2 (datos dentro del formulario)
            formDisplayNombre.textContent = `${paciente.nombre} ${paciente.apellido}`;
            formDisplayDni.textContent = paciente.dni || "-";
            formDisplayMuestra.textContent = paciente.numeroMuestra || "No asignado";

            // Dispara evento para posible validación futura del campo oculto
            nroMuestraInput.dispatchEvent(new Event('input'));

            showAlert("Paciente encontrado.", "success", 2000, "formAlertsCrossmatch");

        } else {
            limpiarPacienteEncontrado();
            showAlert("Paciente no encontrado.", "danger", 3000, "formAlertsCrossmatch");
        }
    } catch (error) {
        hideLoader();
        console.error('Error en buscarPaciente:', error);
        showAlert("Error al cargar la lista de pacientes.", "danger", 4000, "formAlertsCrossmatch");
        limpiarPacienteEncontrado(); // Limpia por si acaso
    }
}

/**
 * Configura los listeners y obtiene referencias del DOM al cargar el módulo.
 */
export function initCrossmatchModule() {
    form = document.getElementById("formCrossmatch");
    if (!form) return;

    // Obtiene referencias del DOM
    hiddenIdInput = document.getElementById("hiddenPacienteIdCM");
    nroMuestraInput = document.getElementById("numeroMuestraCrossmatch"); // Input oculto
    formDisplayNombre = document.getElementById("formDisplayNombreCM");
    formDisplayDni = document.getElementById("formDisplayDniCM");
    formDisplayMuestra = document.getElementById("formDisplayMuestraCM");

    // Asigna listener al botón de búsqueda
    document.getElementById("btnBuscarPacienteCM").addEventListener("click", buscarPaciente);

    // Asigna listener al envío del formulario
    form.addEventListener("submit", async (e) => {
        e.preventDefault();

        const idPaciente = hiddenIdInput.value;
        if (!idPaciente) {
            showAlert("Debe buscar y seleccionar un paciente primero.", "warning", 3000, "formAlertsCrossmatch");
            form.classList.add("was-validated"); // Marca errores si es necesario
            return;
        }

        if (!form.checkValidity()) {
            showAlert("Por favor, complete todos los campos obligatorios.", "warning", 3000, "formAlertsCrossmatch");
            form.classList.add("was-validated");
            return;
        }

        // Prepara los datos para enviar
        const crossmatchData = {
            fecha: form.fecha.value,
            numeroMuestra: form.numeroMuestraCrossmatch.value, // Valor del input oculto
            antiHla1: parseInt(form.antiHla1.value, 10),
            antiHla2: parseInt(form.antiHla2.value, 10),
            resultado: form.resultado.value,
            anticuerposNoConfirmados: form.anticuerposNoConfirmados.value
        };

        try {
            showLoader();
            await guardarCrossmatchAPI(idPaciente, crossmatchData);
            hideLoader();
            showAlert("Crossmatch guardado correctamente.", "success", 3000, "formAlertsCrossmatch");

            // Limpia formulario y estado
            form.reset();
            form.classList.remove("was-validated");
            limpiarPacienteEncontrado();

        } catch (err) {
            hideLoader();
            console.error('Error al guardar Crossmatch:', err);
            showAlert(err.message || "Error al guardar Crossmatch.", "danger", 4000, "formAlertsCrossmatch");
        }
    });
}