import {
  getInformeDsaAPI,
  downloadPdfAPI,
  guardarObservacionDsaAPI,
  getInformeFamiliarAPI,
  getInformeHlaAPI,
  guardarObservacionHlaAPI,
  guardarObservacionFamiliarAPI,
} from "./api.js";
import { resetToHomeView } from "./sidebar.js";
import { showAlert } from "./alerts.js";
import { showLoader, hideLoader } from "./loader.js";

// ==========================================================
// === MÓDULO INFORME DSA
// ==========================================================
export function initInformeDsaModule() {
  const btnBuscar = document.getElementById("btnBuscarInforme");
  const btnDescargar = document.getElementById("btnDescargarPDF");
  const reportVisualizer = document.getElementById("report-visualizer");
  const inputObservaciones = document.getElementById("inputObservaciones");
  
  const tablaClase1Body = document.querySelector("#tabla-clase1 tbody");
  const tablaClase2Body = document.querySelector("#tabla-clase2 tbody");

  let datosActuales = null;

  // 1. EVENTO BUSCAR
  if (btnBuscar) {
    btnBuscar.onclick = async () => {
      const queryDni = document.getElementById("inputDni")?.value;
      if (!queryDni) {
        showAlert("Por favor, ingrese un DNI para buscar.", "warning");
        return;
      }

      showLoader();
      try {
        const data = await getInformeDsaAPI(queryDni);
        datosActuales = data;
        cargarDatosVisualizacionDSA(data);
        // CORREGIDO: Accedemos directo a la raíz, ya no a data.dsa
        if (inputObservaciones) inputObservaciones.value = data.observaciones || ""; 
      } catch (err) {
        console.error(err);
        showAlert(`Error al buscar: ${err.message}`, "danger");
        if (reportVisualizer) reportVisualizer.classList.add("hidden");
      } finally {
        hideLoader();
      }
    };
  }

  // 2. EVENTO DESCARGAR PDF
  if (btnDescargar) {
    btnDescargar.onclick = async () => {
      if (!datosActuales) {
        showAlert("Primero debe buscar un paciente.", "warning");
        return;
      }

      const observaciones = inputObservaciones ? inputObservaciones.value : "";
      
      // NOTA: Para guardar observaciones necesitas el ID. 
      // Si el backend nuevo no lo envía en la raíz, esta línea podría fallar.
      // Por ahora intentamos obtenerlo de donde sea posible o lo omitimos si es null.
      const idDsa = datosActuales.idDsa || (datosActuales.dsa ? datosActuales.dsa.idDsa : null);
      
      // CORREGIDO: Usamos datosActuales.dni
      const dniPaciente = datosActuales.dni || document.getElementById("viz-dni")?.textContent.trim();

      showLoader();
      try {
        // Solo guardamos si tenemos ID
        if(idDsa) {
            await guardarObservacionDsaAPI(idDsa, observaciones);
        }
        
        showAlert("Generando PDF...", "success");

        const url = `http://localhost:8080/api/informes/dsa/pdf?dni=${dniPaciente}`;
        await downloadPdfAPI(url, `informe_dsa_${dniPaciente}.pdf`);

      } catch (err) {
        console.error(err);
        showAlert(`Error: ${err.message}`, "danger");
      } finally {
        hideLoader();
      }
    };
  }

  const breadcrumb = document.getElementById("breadcrumb-inicio");
  if(breadcrumb) breadcrumb.onclick = (e) => { e.preventDefault(); resetToHomeView(); };

  // --- Helper de renderizado CORREGIDO ---
  function cargarDatosVisualizacionDSA(data) {
    const setText = (id, val) => { const el = document.getElementById(id); if(el) el.textContent = val; };
    
    // CORREGIDO: Lectura de propiedades planas (sin .dsa)
    setText("viz-paciente", data.nombrePaciente);
    setText("viz-dni", data.dni);
    setText("viz-muestra", data.muestra);
    setText("viz-fecha", data.fecha); // Agregamos fecha si existe en el HTML
    // setText("viz-medico", data.medicoSolicitante); // Este dato no venía en el DTO nuevo, lo comentamos o lo manejamos

    if (tablaClase1Body) tablaClase1Body.innerHTML = "";
    if (tablaClase2Body) tablaClase2Body.innerHTML = "";

    // data.anticuerpos sigue siendo una lista en la raíz
    const lista = data.anticuerpos || [];
    const clase1 = lista.filter((ac) => ac.tipo === "Clase1");
    const clase2 = lista.filter((ac) => ac.tipo === "Clase2");

    if (tablaClase1Body) {
        clase1.forEach((ac) => {
          tablaClase1Body.innerHTML += `<tr><td>${ac.serologico}</td><td>${ac.alelico}</td><td>${ac.mfi}</td><td>${ac.resultado}</td></tr>`;
        });
    }
    if (tablaClase2Body) {
        clase2.forEach((ac) => {
          tablaClase2Body.innerHTML += `<tr><td>${ac.serologico}</td><td>${ac.alelico}</td><td>${ac.mfi}</td><td>${ac.resultado}</td></tr>`;
        });
    }

    if (reportVisualizer) reportVisualizer.classList.remove("hidden");
  }
}

// ==========================================================
// === MÓDULO INFORME FAMILIAR
// ==========================================================
export function initInformeFamiliarModule() {
  const btnBuscar = document.getElementById("btnBuscarInforme");
  const btnDescargar = document.getElementById("btnDescargarPDF");
  const reportVisualizer = document.getElementById("report-visualizer");
  const inputObservaciones = document.getElementById("inputObservaciones");
  
  let datosActuales = null;

  if (btnBuscar) {
    btnBuscar.onclick = async () => {
      const queryDni = document.getElementById("inputDni")?.value;
      if (!queryDni) { showAlert("Ingrese DNI.", "warning"); return; }
      
      showLoader();
      try {
        const data = await getInformeFamiliarAPI(queryDni);
        datosActuales = data;
        cargarDatosVisualizacionFamiliar(data);
        if(inputObservaciones) inputObservaciones.value = data.notaGeneral || "";
      } catch (err) {
        console.error(err);
        showAlert(`Error: ${err.message}`, "danger");
        if(reportVisualizer) reportVisualizer.classList.add("hidden");
      } finally {
        hideLoader();
      }
    };
  }

  if (btnDescargar) {
    btnDescargar.onclick = async () => {
      if (!datosActuales || !datosActuales.paciente) {
        showAlert("Busque un paciente primero.", "warning"); return;
      }
      const idHlaReferencia = datosActuales.idHlaReferencia;
      const dni = datosActuales.paciente.dni; // Obtenemos DNI para la URL
      const observaciones = inputObservaciones ? inputObservaciones.value : "";
      
      showLoader();
      try {
        if(idHlaReferencia) {
            await guardarObservacionFamiliarAPI(idHlaReferencia, observaciones);
        }
        
        // CORREGIDO: Implementación de descarga PDF
        showAlert("Generando PDF...", "success");
        const url = `http://localhost:8080/api/informes/familiar/pdf?dni=${dni}`;
        await downloadPdfAPI(url, `informe_familiar_${dni}.pdf`);

      } catch (err) {
        showAlert(`Error: ${err.message}`, "danger");
      } finally {
        hideLoader();
      }
    };
  }

  function cargarDatosVisualizacionFamiliar(data) {
    const p = data.paciente;
    const donantes = data.donantes || []; // Ahora coincide con el DTO backend
    
    const vizP = document.getElementById("viz-paciente");
    if(vizP) vizP.textContent = p.nombre || "N/A";

    const tAbc = document.querySelector("#tabla-familiar-abc tbody");
    const tDrq = document.querySelector("#tabla-familiar-drq tbody");
    if(tAbc) tAbc.innerHTML = "";
    if(tDrq) tDrq.innerHTML = "";

    const rowAbc = (actor) => (actor.abc || []).map((v) => `<td>${v||"-"}</td>`).join("");
    const rowDrq = (actor) => (actor.drq || []).map((v) => `<td>${v||"-"}</td>`).join("");

    if(tAbc) tAbc.innerHTML += `<tr><td><strong>Paciente</strong></td><td>${p.muestra||"-"}</td>${rowAbc(p)}</tr>`;
    if(tDrq) tDrq.innerHTML += `<tr><td><strong>Paciente</strong></td><td>${p.muestra||"-"}</td>${rowDrq(p)}</tr>`;

    donantes.forEach(d => {
      if(tAbc) tAbc.innerHTML += `<tr><td>${d.nombre} (${d.vinculo})</td><td>${d.muestra}</td>${rowAbc(d)}</tr>`;
      if(tDrq) tDrq.innerHTML += `<tr><td>${d.nombre} (${d.vinculo})</td><td>${d.muestra}</td>${rowDrq(d)}</tr>`;
    });

    if(reportVisualizer) reportVisualizer.classList.remove("hidden");
  }
}

// ==========================================================
// === MÓDULO INFORME HLA (Inscripción)
// ==========================================================
export function initInformeHlaModule() {
  const btnBuscar = document.getElementById("btnBuscarInforme");
  const btnDescargar = document.getElementById("btnDescargarPDF");
  const reportVisualizer = document.getElementById("report-visualizer");
  const inputObservaciones = document.getElementById("inputObservaciones");
  
  let datosActuales = null;

  if (btnBuscar) {
    btnBuscar.onclick = async () => {
      const queryDni = document.getElementById("inputDni")?.value;
      if (!queryDni) { showAlert("Ingrese DNI.", "warning"); return; }
      
      showLoader();
      try {
        const data = await getInformeHlaAPI(queryDni);
        // data trae { paciente: { ... } } según tu controller
        datosActuales = data; 
        cargarDatosVisualizacionHla(data);
        if(inputObservaciones && data.paciente && data.paciente.hla) {
             inputObservaciones.value = data.paciente.hla.observaciones || "";
        }
      } catch (err) {
        console.error(err);
        showAlert(`Error: ${err.message}`, "danger");
        if(reportVisualizer) reportVisualizer.classList.add("hidden");
      } finally {
        hideLoader();
      }
    };
  }

  if (btnDescargar) {
    btnDescargar.onclick = async () => {
      if (!datosActuales || !datosActuales.paciente) {
        showAlert("Primero busque un paciente.", "warning"); return;
      }
      const idHla = datosActuales.paciente.idHla;
      const dni = datosActuales.paciente.dni;
      const obs = inputObservaciones ? inputObservaciones.value : "";
      
      showLoader();
      try {
        if(idHla) {
            await guardarObservacionHlaAPI(idHla, obs);
        }
        // CORREGIDO: Implementación de descarga PDF
        showAlert("Generando PDF...", "success");
        const url = `http://localhost:8080/api/informes/hla/pdf?dni=${dni}`;
        await downloadPdfAPI(url, `formulario_inscripcion_${dni}.pdf`);

      } catch (err) {
        showAlert(`Error: ${err.message}`, "danger");
      } finally {
        hideLoader();
      }
    };
  }

  function cargarDatosVisualizacionHla(data) {
    const p = data.paciente;
    const set = (id, v) => { const el = document.getElementById(id); if(el) el.textContent = v||"-"; };

    set("viz-paciente", p.nombre);
    set("viz-dni", p.dni);
    set("viz-muestra", p.muestra);
    set("viz-grupo", p.grupoSanguineo);
    
    // Asumiendo que p.hla existe y tiene estructura plana o anidada según tu DTO HlaSimpleDTO
    if (p.hla) {
        set("viz-hla-a1", p.hla.a1); set("viz-hla-a2", p.hla.a2);
        set("viz-hla-b1", p.hla.b1); set("viz-hla-b2", p.hla.b2);
        set("viz-hla-bw1", p.hla.bw1); set("viz-hla-bw2", p.hla.bw2);
        set("viz-hla-c1", p.hla.c1); set("viz-hla-c2", p.hla.c2);
        set("viz-hla-dr1", p.hla.dr1); set("viz-hla-dr2", p.hla.dr2);
        set("viz-hla-dq1", p.hla.dq1); set("viz-hla-dq2", p.hla.dq2);
        set("viz-hla-dp1", p.hla.dp1); set("viz-hla-dp2", p.hla.dp2);
    }

    if(reportVisualizer) reportVisualizer.classList.remove("hidden");
  }
}