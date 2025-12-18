import { uploadFileAPI } from './api.js'; 
import { showAlert } from './alerts.js';

// ID del contenedor de alertas (definido en carga_dsa.html)
const ALERTS_CONTAINER_ID = 'formAlertsDSA';

/**
 * Maneja el envío del formulario de carga de DSA
 */
async function handleFormSubmit(e) {
    e.preventDefault(); // Prevenimos el envío real
    
    const form = e.target; // ¡Obtenemos el form desde el evento!
    if (!form.checkValidity()) {
        e.stopPropagation();
        form.classList.add('was-validated');
        showAlert('Por favor, selecciona un archivo válido.', 'warning', 3000, ALERTS_CONTAINER_ID);
        return;
    }

    const fileInput = document.getElementById('dsa-file-input');
    const file = fileInput.files[0];

    if (!file) {
        showAlert('No se seleccionó ningún archivo.', 'warning', 3000, ALERTS_CONTAINER_ID);
        return;
    }

    // Buscamos el botón y los spinners DENTRO del handler
    const btnSubmit = document.getElementById('btn-subir-dsa');
    const btnText = btnSubmit?.querySelector('.btn-text');
    const btnSpinner = btnSubmit?.querySelector('.btn-spinner');

    // Deshabilitar botón y mostrar spinner
    if (btnSubmit) {
        btnSubmit.disabled = true;
        btnText.style.display = 'none';
        btnSpinner.style.display = 'inline-block';
    }

    try {
        const formData = new FormData();
        formData.append('file', file);

        // Llamamos a la API de subida de archivos
        const responseText = await uploadFileAPI('/reports/upload-dsa', formData);

        // Mostramos el mensaje de éxito
        showAlert(responseText, 'success', 4000, ALERTS_CONTAINER_ID);
        
        // --- ¡AQUÍ ESTÁ LA CORRECCIÓN! ---
        // Usamos e.target (que es el 'form') en lugar de la variable 'form'
        e.target.reset(); 
        form.classList.remove('was-validated');

    } catch (error) {
        // Mostramos el error
        console.error('Error al subir el archivo:', error);
        showAlert(error.message || 'Error desconocido al subir el archivo.', 'danger', 5000, ALERTS_CONTAINER_ID);
    } finally {
        // Habilitar botón y ocultar spinner
        if (btnSubmit) {
            btnSubmit.disabled = false;
            btnText.style.display = 'inline-block';
            btnSpinner.style.display = 'none';
        }
    }
}


export function initCargaDsaModule() {
    const form = document.getElementById("form-carga-dsa");
    if (!form) return;

    // Adjuntamos el listener al formulario
    form.addEventListener("submit", handleFormSubmit);
}

