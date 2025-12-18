import { showModal } from "./modal.js";
import { getDsaResultadosByPacienteId, getHlaByDonanteId } from "./api.js";

// Toggle loader global (asume que loader.js lo define)
const toggleLoader = window.toggleLoader || ((show) => {
    const loader = document.getElementById('loading-indicator');
    if (loader) {
        if (show) {
            loader.classList.remove('d-none');
            loader.classList.add('d-flex');
        } else {
            loader.classList.add('d-none');
            loader.classList.remove('d-flex');
        }
    }
});


// Estado Global de la Aplicación
const state = {
    currentStep: 1,
    patientId: null,
    patientName: null,
    dsaResults: [], // Resultados de DSA del paciente (Receptor)
    donorHla: [],    // Tipificación HLA del donante
    donorId: null,
    donorName: null,
    matches: new Set(), // Set de antígenos DSA que coinciden con el HLA del donante
};

// Referencias del DOM (Obtenidas al inicializar)
let contentArea, resultsArea, loadingIndicator, stepTitle, stepSubtitle, flowContainer;

// --- Funciones Auxiliares ---

/**
 * Implementación de la función simulateApiCall
 * Muestra/oculta el loader global. En producción, solo envolvería fetch.
 * @returns {Promise<void>}
 */
const simulateApiCall = () => {
    toggleLoader(true);
    return new Promise(resolve => {
        setTimeout(() => {
            toggleLoader(false);
            resolve();
        }, 800);
    });
};

/**
 * Muestra un mensaje simple usando el modal global.
 * @param {string} message 
 */
function alertUser(message) {
    if (typeof showModal === 'function') {
        showModal('Aviso del Sistema', message, 'Aceptar', null, 'primary');
    } else {
        // Fallback si showModal no está disponible
        console.error("showModal no está disponible.");
    }
}


// --- LÓGICA DEL MÓDULO (INICIO) ---

/**
 * Exporta la función de inicialización.
 * Inicializa las referencias del DOM y comienza el flujo.
 * @exports
 */
export function initCrossmatchModule() {
    // Las referencias deben ser al contenedor principal de la vista
    flowContainer = document.getElementById('flow-container'); 
    resultsArea = document.getElementById('results-area');
    loadingIndicator = document.getElementById('loading-indicator');
    
    if (!flowContainer || !resultsArea) {
        console.error("Error al inicializar: No se encontraron los contenedores principales (flow-container o results-area).");
        return;
    }
    
    renderStep1();
}

/**
 * Renderiza la pantalla inicial con la estética de búsqueda por DNI/Muestra (como los informes).
 */
/**
 * (VERSIÓN CORREGIDA - Colspan 4)
 * Renderiza la pantalla inicial.
 */
function renderStep1() {
    state.currentStep = 1;
    
    flowContainer.innerHTML = `
        <div class="card mb-4">
            <div class="card-header bg-primary text-white">
                <p id="step-title" class="h5 fw-semibold mb-0">Paso 1: Selección de Paciente Receptor</p>
            </div>
            <div class="card-body">
                <div class="row">
                    <div class="col-md-5">
                        <input type="text" id="inputDni" class="form-control" placeholder="Buscar por DNI...">
                    </div>
                    <div class="col-md-5">
                        <input type="text" id="inputMuestra" class="form-control" placeholder="Buscar por N° Muestra...">
                    </div>
                    <div class="col-md-2">
                        <button id="btnBuscarPaciente" class="btn btn-primary w-100">Cargar DSA</button>
                    </div>
                </div>
                <div id="content-area" class="mt-4"></div>
            </div>
        </div>
    `;

    contentArea = document.getElementById('content-area');
    stepTitle = document.getElementById('step-title');
    stepSubtitle = document.getElementById('step-subtitle');
    document.getElementById('btnBuscarPaciente').addEventListener('click', loadPatient);
    
    resultsArea.classList.add('d-none');
    document.getElementById('patient-info').textContent = '';
    
    const dsaBody = document.getElementById('dsa-table-body');
    if (dsaBody) {
        dsaBody.innerHTML = '<tr><td colspan="4" class="text-muted">Cargue un paciente...</td></tr>';
    }
    const hlaBody = document.getElementById('hla-table-body');
    if (hlaBody) {
        hlaBody.innerHTML = '<tr><td colspan="2" class="text-muted">Cargue un donante...</td></tr>';
    }
    
    document.getElementById('donor-info').textContent = '';
    state.matches.clear();
}

/**
 * Simula la carga del paciente y pasa al paso 2.
 */
async function loadPatient() {
    const dniInput = document.getElementById('inputDni').value.trim();
    const muestraInput = document.getElementById('inputMuestra').value.trim();
    const patientId = dniInput || muestraInput; // Usar DNI o Muestra como ID de búsqueda

    if (!patientId) {
        alertUser("Por favor, ingrese el DNI o el Número de Muestra del paciente receptor.");
        return;
    }

    try {
        await simulateApiCall(); // Muestra el loader
        
        // Usamos el ID (DNI o Muestra) para la llamada a la API
        // Se asume que el backend de Spring Boot maneja la búsqueda por DNI o Muestra
        const dsaResultados = await getDsaResultadosByPacienteId(patientId);

        if (!dsaResultados || dsaResultados.length === 0) {
            alertUser(`No se encontraron resultados DSA para el paciente DNI: ${patientId}.`);
            return;
        }

        // Simulación de datos del paciente
        const mockPatientName = `Paciente (DNI/Muestra: ${patientId})`;
        
        state.patientId = patientId;
        state.patientName = mockPatientName;
        state.dsaResults = dsaResultados; 
        
        // Muestra la sección lateral de resultados
        resultsArea.classList.remove('d-none');
        document.getElementById('patient-info').textContent = `DNI: ${state.patientId}, Nombre: ${state.patientName}`;
        renderDSAList();

        state.currentStep = 2;
        renderStep2();

    } catch (error) {
        console.error("Error al cargar paciente y DSA:", error);
        alertUser("Error al cargar los datos del paciente. Verifique la información de búsqueda.");
    }
}


/**
 * Renderiza la pantalla para buscar o ingresar el donante (Paso 2).
 */
function renderStep2() {
    flowContainer.innerHTML = `
        <div class="card mb-4">
            <div class="card-header bg-success text-white">
                <p id="step-title" class="h5 fw-semibold mb-0">Paso 2: Gestión y Carga de Donante</p>
            </div>
            <div class="card-body">
                <p id="step-subtitle" class="text-muted small mb-3">
                    Busque el donante por DNI o ingrese su tipificación HLA manualmente para el Crossmatch.
                </p>
                <div id="content-area">
                    <div class="row g-4">
                        <div class="col-12">
                            <div class="card card-body bg-light">
                                <h3 class="h6 fw-semibold mb-3 text-secondary">1. Buscar Donante por DNI</h3>
                                <div class="input-group">
                                    <input id="donor-id-input" type="text" placeholder="DNI del Donante (existente en BD)" 
                                        class="form-control">
                                    <button id="search-donor-btn" class="btn btn-success">
                                        Buscar HLA
                                    </button>
                                </div>
                            </div>
                        </div>

                        <div class="col-12">
                            <div class="card card-body bg-light">
                                <h3 class="h6 fw-semibold mb-3 text-secondary">2. Ingreso Manual de HLA (Temporal)</h3>
                                
                                <div id="manual-hla-form" class="row g-3">
                                    <div class="col-md-2"><label class="form-label small">Locus A1</label><input type="text" id="manual-a1" class="form-control form-control-sm"></div>
                                    <div class="col-md-2"><label class="form-label small">Locus A2</label><input type="text" id="manual-a2" class="form-control form-control-sm"></div>
                                    <div class="col-md-2"><label class="form-label small">Locus B1</label><input type="text" id="manual-b1" class="form-control form-control-sm"></div>
                                    <div class="col-md-2"><label class="form-label small">Locus B2</label><input type="text" id="manual-b2" class="form-control form-control-sm"></div>
                                    <div class="col-md-2"><label class="form-label small">Locus C1</label><input type="text" id="manual-c1" class="form-control form-control-sm"></div>
                                    <div class="col-md-2"><label class="form-label small">Locus C2</label><input type="text" id="manual-c2" class="form-control form-control-sm"></div>
                                    <div class="col-md-2"><label class="form-label small">Locus DR1</label><input type="text" id="manual-dr1" class="form-control form-control-sm"></div>
                                    <div class="col-md-2"><label class="form-label small">Locus DR2</label><input type="text" id="manual-dr2" class="form-control form-control-sm"></div>
                                    <div class="col-md-2"><label class="form-label small">Locus DQA1</label><input type="text" id="manual-dqa1" class="form-control form-control-sm"></div>
                                    <div class="col-md-2"><label class="form-label small">Locus DQA2</label><input type="text" id="manual-dqa2" class="form-control form-control-sm"></div>
                                    <div class="col-md-2"><label class="form-label small">Locus DQB1</label><input type="text" id="manual-dqb1" class="form-control form-control-sm"></div>
                                    <div class="col-md-2"><label class="form-label small">Locus DQB2</label><input type="text" id="manual-dqb2" class="form-control form-control-sm"></div>
                                    <div class="col-md-2"><label class="form-label small">Locus DPA1</label><input type="text" id="manual-dpa1" class="form-control form-control-sm"></div>
                                    <div class="col-md-2"><label class="form-label small">Locus DPA2</label><input type="text" id="manual-dpa2" class="form-control form-control-sm"></div>
                                    <div class="col-md-2"><label class="form-label small">Locus DPB1</label><input type="text" id="manual-dpb1" class="form-control form-control-sm"></div>
                                    <div class="col-md-2"><label class="form-label small">Locus DPB2</label><input type="text" id="manual-dpb2" class="form-control form-control-sm"></div>
                                </div>
                                
                                <button id="manual-hla-btn" class="btn btn-warning w-100 mt-3">
                                    Cargar HLA Manual y Ejecutar Crossmatch
                                </button>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    `;

    // Re-obtener referencias después de la inyección
    contentArea = document.getElementById('content-area');
    stepTitle = document.getElementById('step-title');
    stepSubtitle = document.getElementById('step-subtitle');

    // Adjuntar eventos
    document.getElementById('search-donor-btn').addEventListener('click', searchDonor);
    document.getElementById('manual-hla-btn').addEventListener('click', captureManualHla);

    document.getElementById('donor-info').textContent = 'Esperando carga de donante...';
    
    const hlaBody = document.getElementById('hla-table-body'); 
    if (hlaBody) {
        hlaBody.innerHTML = '<tr><td colspan="2" class="text-muted">Cargue un donante...</td></tr>'; 
    }
}


/**
 * Busca el donante existente usando la API.
 */
async function searchDonor() {
    const idInput = document.getElementById('donor-id-input');
    const donorId = idInput.value.trim();

    if (!donorId) {
        alertUser("Por favor, ingrese el DNI del donante.");
        return;
    }

    try {
        await simulateApiCall(); // Muestra el loader
        
        // Llama a la API real para obtener el HLA del donante
        // donorData ahora tiene el formato: { hla: [...], nombreDonante: "..." }
        const donorData = await getHlaByDonanteId(donorId); 
        
        if (!donorData || !donorData.hla || donorData.hla.length === 0) {
            alertUser(`Donante DNI: ${donorId} encontrado, pero sin tipificación HLA registrada.`);
            return;
        }

        state.donorId = donorId;
        // Usar el nombreDonante que viene del DTO
        state.donorName = donorData.nombreDonante; 
        state.donorHla = donorData.hla.map(h => h.toUpperCase()); 
        
        executeCrossmatch();

    } catch (error) {
        console.error("Error al buscar donante:", error);
        alertUser("Error al buscar el donante. Podría no existir en el sistema o la conexión falló.");
    }
}

/**
 * Captura la entrada manual de HLA desde el formulario detallado (sin guardar en BD).
 */

function captureManualHla() {
    
    // Objeto para mapear los IDs de los inputs a sus prefijos serológicos
    const hlaInputMapping = {
        'manual-a1': 'A', 'manual-a2': 'A',
        'manual-b1': 'B', 'manual-b2': 'B',
        'manual-c1': 'C', 'manual-c2': 'C',
        'manual-dr1': 'DR', 'manual-dr2': 'DR',
        'manual-dqa1': 'DQA', 'manual-dqa2': 'DQA',
        'manual-dqb1': 'DQB', 'manual-dqb2': 'DQB',
        'manual-dpa1': 'DPA', 'manual-dpa2': 'DPA',
        'manual-dpb1': 'DPB', 'manual-dpb2': 'DPB'
    };

    const hlaArray = [];
    const inputIds = Object.keys(hlaInputMapping); // ej: ['manual-a1', 'manual-b1', ...]

    inputIds.forEach(id => {
        const input = document.getElementById(id);
        if (input) {
            const value = input.value.trim();
            // Solo procesamos si hay un valor
            if (value.length > 0 && value.toUpperCase() !== 'NULL') {
                
                // Si el valor ya es alélico (contiene *), lo usamos tal cual
                if (value.includes('*')) {
                    // (Opcional: aquí podríamos truncarlo, pero por ahora lo dejamos)
                    hlaArray.push(value.toUpperCase());
                } else {
                    // Es serológico (ej: "2"). Le añadimos el prefijo (ej: "A")
                    const prefix = hlaInputMapping[id]; // ej: "A"
                    hlaArray.push(prefix + value); // ej: "A" + "2" = "A2"
                }
            }
        }
    });

    // Filtramos duplicados que podrían ocurrir (ej: A2 y A2)
    const uniqueHlaArray = [...new Set(hlaArray)];

    if (uniqueHlaArray.length === 0) {
        alertUser("Por favor, ingrese al menos un antígeno HLA en el formulario manual.");
        return;
    }
    
    state.donorId = 'TEMP-' + Math.random().toString(36).substring(2, 6).toUpperCase();
    state.donorName = 'Donante Manual (No Guardado)';
    state.donorHla = uniqueHlaArray;

    // Imprime en la consola del navegador para depurar (¡muy útil!)
    console.log("Donante HLA (Manual-Serológico):", state.donorHla);

    executeCrossmatch();
}

/**
 * Realiza la comparación entre DSA y HLA del donante (Paso 3).
 */
function executeCrossmatch() {

    const donorHla = state.donorHla;
    const dsa = state.dsaResults;
    const matches = new Set();

    donorHla.forEach(hlaAntigen => {
        const isMatch = dsa.some(dsaItem => dsaItem.serologico === hlaAntigen);
        if (isMatch) {
            matches.add(hlaAntigen);
        }
    });
    state.matches = matches;
    flowContainer.innerHTML = `
        <div class="card mb-4">
            <div class="card-header bg-danger text-white">
                <p id="step-title" class="h5 fw-semibold mb-0">Paso 3: Resultado del Crossmatch Virtual</p>
            </div>
            <div class="card-body">
                <p id="step-subtitle" class="text-muted small mb-3">
                    Los antígenos del Donante encontrados en el DSA del Receptor están resaltados en ROJO.
                </p>
                <div id="content-area">
                    <div class="alert alert-danger" role="alert">
                        <h4 class="alert-heading">Análisis Completado</h4>
                        
                        <p>Se encontraron <span class="fw-bolder text-danger">${state.matches.size}</span> coincidencias directas (DSA) con los antígenos HLA del donante.</p>
                        
                        <hr>
                        <p class="mb-0 small">Revise las tablas para ver los detalles de los DSA positivos.</p>
                    </div>
                    <button id="btnReiniciarCrossmatch" class="btn btn-secondary mt-3">
                        Reiniciar Crossmatch
                    </button>
                </div>
            </div>
        </div>
    `;
    
    document.getElementById('btnReiniciarCrossmatch').addEventListener('click', renderStep1);

    contentArea = document.getElementById('content-area');
    stepTitle = document.getElementById('step-title');
    stepSubtitle = document.getElementById('step-subtitle');

    // Actualizar vistas de Donante y DSA con el resaltado
    renderHLAList();
    renderDSAList();
}


/**
 * Renderiza la lista de DSA con el resaltado de coincidencias.
 */
function renderDSAList() {
    const dsaTableBody = document.getElementById('dsa-table-body');
    if (!dsaTableBody) return; 
    
    dsaTableBody.innerHTML = ''; 

    if (!state.dsaResults || state.dsaResults.length === 0) {
        dsaTableBody.innerHTML = '<tr><td colspan="4" class="text-muted">No hay DSA registrados para este paciente.</td></tr>';
        return;
    }

    const sortedDSA = [...state.dsaResults].sort((a, b) => (b.mfi || 0) - (a.mfi || 0));

    sortedDSA.forEach(item => {
        const isMatch = state.matches.has(item.serologico);
        const highlightClass = isMatch ? 'table-danger highlight-match' : '';
        const rowHtml = `
            <tr class="${highlightClass}">
                <td class="fw-bold text-dark">${item.serologico}</td> <td>${item.mfi || 0}</td> <td>${item.resultado || 'N/A'}</td> <td>
                    ${isMatch ? '<span class="badge bg-danger rounded-pill">DSA PELIGROSO</span>' : ''}
                </td>
            </tr>
        `;
        dsaTableBody.innerHTML += rowHtml;
    });
}

/**
 * Renderiza la lista de HLA del donante.
 */
function renderHLAList() {
    const hlaTableBody = document.getElementById('hla-table-body');
    if (!hlaTableBody) return; // Salir si la tabla no existe

    hlaTableBody.innerHTML = ''; // Limpiamos la tabla

    document.getElementById('donor-info').textContent = `ID: ${state.donorId}, Nombre: ${state.donorName}`;
    
    if (!state.donorHla || state.donorHla.length === 0) {
        hlaTableBody.innerHTML = '<tr><td colspan="2" class="text-muted">No se cargó HLA para este donante.</td></tr>';
        return;
    }

    state.donorHla.forEach(hla => {
        const isMatch = state.matches.has(hla);
        
        const highlightClass = isMatch ? 'table-danger' : ''; // Resaltar fila
        
        const itemHtml = `
            <tr class="${highlightClass}">
                <td class="fw-bold text-dark">${hla}</td>
                <td>
                    ${isMatch ? '<span class="badge bg-danger rounded-pill">COINCIDE CON DSA</span>' : '<span class="badge bg-secondary rounded-pill">No Coincide</span>'}
                </td>
            </tr>
        `;
        hlaTableBody.innerHTML += itemHtml;
    });
}