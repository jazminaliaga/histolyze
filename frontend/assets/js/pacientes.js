import { getPacientes, crearPaciente } from "./api.js";
import { showAlert } from "./alerts.js";
import { showLoader, hideLoader } from "./loader.js";
import { loadModuleAndInit } from "./sidebar.js";
import { initAntecedentesModule } from "./antecedentes.js";
import { getUsuarioLogueado } from "./auth.js";

function limitarFechas() {
  const hoy = new Date().toISOString().split("T")[0];
  const form = document.getElementById("formPaciente");
  if (form) {
    form.querySelectorAll('input[type="date"]').forEach((input) => {
      input.max = hoy;
    });
  }
}

export function initPacienteModule() {
  const form = document.getElementById("formPaciente");
  if (form) {
    initPacienteForm(form);
    initAntecedentesModule();
    limitarFechas();
  }

  if (document.getElementById("pacientesTable")) {
    renderPacientes();
    initSearchInputs();
  }
}

function initPacienteForm(form) {
  form.addEventListener("submit", async (e) => {
    e.preventDefault();
    e.stopPropagation();

    if (!form.checkValidity()) {
      form.classList.add("was-validated");
      showAlert(
        "Por favor, completa todos los campos correctamente",
        "warning",
        3000,
        "formAlerts"
      );
      return;
    }

    const tuvoEmbarazos =
      form.querySelector('input[name="embarazos"]:checked')?.value === "si";
    const tuvoTransfusiones =
      form.querySelector('input[name="transfusiones"]:checked')?.value === "si";
    const tuvoTrasplantes =
      form.querySelector('input[name="trasplantes"]:checked')?.value === "si";
    // Lista de Transfusiones
    const transfusionesList = [];
    // Si el usuario marcó "sí" y completó la fecha...
    if (tuvoTransfusiones && form.fechaTransfusiones.value) {
      // Creamos un objeto Transfusion y lo añadimos a la lista
      transfusionesList.push({
        fecha: form.fechaTransfusiones.value,
        tipo: form.tipoTransfusion.value || null,
      });
    }

    // Lista de Trasplantes
    const trasplantesList = [];
    if (tuvoTrasplantes && form.fechaTrasplante.value) {
      // Creamos el objeto complejo HLA aquí
      const hlaDonanteObjeto = {
        a1: form.hlaA1.value,
        a2: form.hlaA2.value,
        b1: form.hlaB1.value,
        b2: form.hlaB2.value,
        c1: form.hlaC1.value,
        c2: form.hlaC2.value,
        dr1: form.hlaDR1.value,
        dr2: form.hlaDR2.value,
        dqa1_1: form.hlaDQA11.value,
        dqa1_2: form.hlaDQA12.value,
        dqb1_1: form.hlaDQB11.value,
        dqb1_2: form.hlaDQB12.value,
        dpa1_1: form.hlaDPA11.value,
        dpa1_2: form.hlaDPA12.value,
        dpb1_1: form.hlaDPB11.value,
        dpb1_2: form.hlaDPB12.value,
      };

      trasplantesList.push({
        fecha: form.fechaTrasplante.value,
        tipo: form.tipoTrasplante.value || null,
        hlaDonante: hlaDonanteObjeto,
      });
    } // 3. Construir el payload final con la ESTRUCTURA CORRECTA

    const payload = {
      // Datos del Paciente
      nombre: form.nombre.value,
      apellido: form.apellido.value,
      numeroMuestra: form.muestra.value,
      dni: form.dni.value,
      fechaNacimiento: form.fechaNacimiento.value,
      domicilio: form.domicilio.value,
      telefono: form.telefono.value,
      mutual: form.mutual.value,
      centroTx: form.tx.value,
      centroDialisis: form.dialisis.value,
      medicoSolicitante: form.medico.value,

      antecedentes: [
        {
          diagnostico: form.diagnostico.value,
          medicacion: form.medicacion.value,
          fechaComienzoHemodialisis: form.fechaHemodialisis.value,
          embarazos: tuvoEmbarazos,
          cantidadEmbarazos: tuvoEmbarazos
            ? form.cantidadEmbarazos.value || 0
            : 0,
          tuvoTransfusiones: tuvoTransfusiones, 
          fechaTransfusiones: form.fechaTransfusiones.value, 
          procesoDonacion: form.pdTransfusiones.value,
          tuvoTrasplantesPrevios: tuvoTrasplantes, 
          grupoSanguineo: form.grupoSanguineo.value|| null, 
          hlaDonante: {
            a1: form.hlaA1.value,
            a2: form.hlaA2.value,
            b1: form.hlaB1.value,
            b2: form.hlaB2.value,
            c1: form.hlaC1.value,
            c2: form.hlaC2.value,
            dr1: form.hlaDR1.value,
            dr2: form.hlaDR2.value,
            dqa1_1: form.hlaDQA11.value,
            dqa1_2: form.hlaDQA12.value,
            dqb1_1: form.hlaDQB11.value,
            dqb1_2: form.hlaDQB12.value,
            dpa1_1: form.hlaDPA11.value,
            dpa1_2: form.hlaDPA12.value,
            dpb1_1: form.hlaDPB11.value,
            dpb1_2: form.hlaDPB12.value,
          }, 

          listaTransfusiones: transfusionesList,
          listaTrasplantes: trasplantesList,
        },
      ],
    };

    try {
      showLoader();
      await crearPaciente(payload);
      hideLoader();
      showAlert(
        "Paciente guardado correctamente",
        "success",
        3000,
        "formAlerts"
      );
      form.reset();
      form.classList.remove("was-validated");
      setTimeout(() => loadModuleAndInit("pacientes_historial"), 1500);
    } catch (err) {
      hideLoader();
      console.error(
        "Error al guardar el paciente:",
        err.response ? err.response.data : err.message
      );
      showAlert(
        err.response?.data?.message ||
          err.message ||
          "Error al guardar el paciente",
        "danger",
        3000,
        "formAlerts"
      );
    }
  });
}

function initSearchInputs() {
  const searchDni = document.getElementById("searchDni");
  if (searchDni) {
    searchDni.addEventListener("input", renderPacientes);
  }

  const searchMuestra = document.getElementById("searchMuestra");
  if (searchMuestra) {
    searchMuestra.addEventListener("input", renderPacientes);
  }
}

export async function renderPacientes() {
  const tbody = document.querySelector("#pacientesTable tbody");
  if (!tbody) return;

  try {
    const pacientes = await getPacientes();
    const dniFilter = document.getElementById("searchDni")?.value.trim() || "";
    const muestraFilter =
      document.getElementById("searchMuestra")?.value.trim() || "";

    tbody.innerHTML = "";

    pacientes
      .filter(
        (p) =>
          (!dniFilter || p.dni.includes(dniFilter)) &&
          (!muestraFilter || (p.numeroMuestra || "").includes(muestraFilter))
      )
      .forEach((p) => {
        // 1. Crear la fila (esto ya lo tenías)
        const tr = document.createElement("tr");
        tr.dataset.id = p.idPaciente;

        // 2. Función auxiliar para crear celdas de forma segura
        const createCell = (text) => {
          const td = document.createElement("td");
          td.textContent = text || ""; // 
          return td;
        };

        // 3. Añadir todas las celdas de datos
        tr.appendChild(createCell(p.idPaciente));
        tr.appendChild(createCell(p.nombre));
        tr.appendChild(createCell(p.apellido));
        tr.appendChild(createCell(p.dni));
        tr.appendChild(createCell(p.numeroMuestra));
        tr.appendChild(createCell(p.centroTx));
        tr.appendChild(createCell(p.medicoSolicitante));

        // 4. Crear la celda especial de "Acciones"
        const tdAcciones = document.createElement("td");
        tdAcciones.classList.add("text-end");

        const btnVer = document.createElement("button");
        btnVer.classList.add("btn", "btn-sm", "btn-info", "btn-ver");
        btnVer.textContent = "Ver Perfil";

        // 5. Añadir el listener DIRECTAMENTE al botón
        btnVer.addEventListener("click", () => verPerfilPaciente(p.idPaciente));

        tdAcciones.appendChild(btnVer);
        tr.appendChild(tdAcciones);

        // 6. Añadir la fila completa y segura al tbody
        tbody.appendChild(tr);
      });
  } catch (err) {
    console.error("Error al renderizar pacientes:", err);
    showAlert("Error al cargar pacientes", "danger");
  }
}

function verPerfilPaciente(pacienteId) {
  loadModuleAndInit("pacientes_detalle", pacienteId);
}

export function initHistorialPacientes() {
  renderPacientes();
  initSearchInputs();
}
