/**
 * Lista de campos que son REQUERIDOS en el HTML.
 * Necesitamos saber esto para poder quitar/poner 'required' dinámicamente
 * y que la validación de Bootstrap funcione correctamente.
 */
const requiredFields = new Set([
  "cantidadEmbarazos",
  "fechaTransfusiones",
  "tipoTransfusion",
  "pdTransfusiones",
  "fechaTrasplante",
  "tipoTrasplante",
  "grupoSanguineo",
]);

/**
 * Función auxiliar para habilitar/deshabilitar un grupo de campos.
 * También gestiona el atributo 'required' para la validación de Bootstrap.
 *
 * @param {HTMLElement[]} fieldElements - Array de elementos del DOM (inputs, selects).
 * @param {boolean} disabled - True para deshabilitar, false para habilitar.
 */
function toggleFields(fieldElements, disabled) {
  fieldElements.forEach((el) => {
    if (el) {
      el.disabled = disabled;

      if (disabled) {
        // Si se deshabilita: limpiar valor, resetear select y quitar 'required'
        el.value = "";
        if (el.tagName === "SELECT") {
          el.selectedIndex = 0;
        }
        if (requiredFields.has(el.id)) {
          el.required = false;
        }
      } else {
        // Si se habilita: restaurar 'required' si estaba en la lista
        if (requiredFields.has(el.id)) {
          el.required = true;
        }
      }
    }
  });
}

/**
 * Inicializa los listeners para los campos de antecedentes.
 * Se encarga de habilitar/deshabilitar campos dependientes.
 */
export function initAntecedentesModule() {
  
  // --- Lógica de Embarazos ---
  const radiosEmbarazos = document.querySelectorAll('input[name="embarazos"]');
  const fieldsEmbarazos = [document.getElementById("cantidadEmbarazos")];

  const handleEmbarazosChange = () => {
    const siChecked = document.getElementById("embarazosSi").checked;
    toggleFields(fieldsEmbarazos, !siChecked);
  };
  radiosEmbarazos.forEach((radio) => radio.addEventListener("change", handleEmbarazosChange));
  handleEmbarazosChange(); // Ejecutar al inicio

  // --- Lógica de Transfusiones ---
  const radiosTransfusiones = document.querySelectorAll('input[name="transfusiones"]');
  const fieldsTransfusiones = [
    document.getElementById("fechaTransfusiones"),
    document.getElementById("tipoTransfusion"),
  ];

  const handleTransfusionesChange = () => {
    const siChecked = document.getElementById("transfusionesSi").checked;
    toggleFields(fieldsTransfusiones, !siChecked);
  };
  radiosTransfusiones.forEach((radio) => radio.addEventListener("change", handleTransfusionesChange));
  handleTransfusionesChange(); // Ejecutar al inicio

  // --- Lógica de Trasplantes (La que pediste) ---
  const radiosTrasplantes = document.querySelectorAll('input[name="trasplantes"]');
  const fieldsTrasplantes = [
    document.getElementById("pdTransfusiones"),
    document.getElementById("fechaTrasplante"),
    document.getElementById("tipoTrasplante"), // Este faltaba en tu versión
    document.getElementById("grupoSanguineo"),
    // Campos HLA (no son 'required', pero deben bloquearse)
    document.getElementById("hlaA1"),
    document.getElementById("hlaA2"),
    document.getElementById("hlaB1"),
    document.getElementById("hlaB2"),
    document.getElementById("hlaC1"),
    document.getElementById("hlaC2"),
    document.getElementById("hlaDR1"),
    document.getElementById("hlaDR2"),
    document.getElementById("hlaDQA11"),
    document.getElementById("hlaDQA12"),
    document.getElementById("hlaDQB11"),
    document.getElementById("hlaDQB12"),
    document.getElementById("hlaDPA11"),
    document.getElementById("hlaDPA12"),
    document.getElementById("hlaDPB11"),
    document.getElementById("hlaDPB12"),
  ];

  const handleTrasplantesChange = () => {
    const siChecked = document.getElementById("trasplantesSi").checked;
    toggleFields(fieldsTrasplantes, !siChecked);
  };
  radiosTrasplantes.forEach((radio) => radio.addEventListener("change", handleTrasplantesChange));
  handleTrasplantesChange(); // Ejecutar al inicio para setear estado por defecto
}