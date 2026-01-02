import { getToken, logout } from "./auth.js";

const API_BASE = "http://localhost:8080/api";

async function fetchAPI(endpoint, options = {}) {
  const token = getToken();
  const headers = {
    "Content-Type": "application/json",
    ...options.headers,
  };

  if (token) {
    headers["Authorization"] = `Bearer ${token}`;
  }

  try {
    const response = await fetch(`${API_BASE}${endpoint}`, {
      ...options,
      headers,
    });

    if (!response.ok) {
      if (response.status === 401) {
        alert("Tu sesión ha expirado. Por favor, inicia sesión de nuevo.");
        logout();
      } else if (response.status === 403) {
        alert("Error: No tienes permiso para realizar esta acción.");
      }
      const errorText = await response.text();
      throw new Error(errorText || `Error en la petición: ${response.status}`);
    }

    if (response.status === 204) {
      return { success: true };
    }

    return await response.json();
  } catch (err) {
    console.error(`Error en fetchAPI para ${endpoint}:`, err);
    throw err;
  }
}

export async function uploadFileAPI(endpoint, formData) {
  const token = getToken();
  const headers = {};

  if (token) {
    headers["Authorization"] = `Bearer ${token}`;
  }

  try {
    const response = await fetch(`${API_BASE}${endpoint}`, {
      method: "POST",
      body: formData, // Enviamos el FormData
      headers: headers,
    });

    // Leemos la respuesta como TEXTO
    const responseText = await response.text();

    if (!response.ok) {
      if (response.status === 401 || response.status === 403) {
        alert("Tu sesión ha expirado. Por favor, inicia sesión de nuevo.");
        logout();
      }
      throw new Error(
        responseText || `Error en la petición: ${response.status}`
      );
    }

    return responseText;
  } catch (err) {
    console.error(`Error en uploadFileAPI para ${endpoint}:`, err);
    throw err;
  }
}

// --- Autenticación ---
export async function loginAPI(dni, password) {
  const response = await fetch(`${API_BASE}/auth/login`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ dni, password }),
  });
  if (!response.ok) {
    throw new Error("Credenciales inválidas");
  }
  return response.json();
}

export async function registerAPI(usuario) {
  const response = await fetch(`${API_BASE}/auth/register`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(usuario),
  });
  if (!response.ok) {
    const errorText = await response.text();
    throw new Error(errorText || "Error al registrar usuario");
  }
  return response.json();
}

// --- Pacientes ---
export function getPacientes() {
  return fetchAPI("/pacientes");
}
export function getPacienteById(id) {
  return fetchAPI(`/pacientes/${id}`);
}
export function crearPaciente(paciente) {
  return fetchAPI("/pacientes", {
    method: "POST",
    body: JSON.stringify(paciente),
  });
}
export function updatePaciente(id, pacienteData) {
  return fetchAPI(`/pacientes/${id}`, {
    method: "PUT",
    body: JSON.stringify(pacienteData),
  });
}

// --- HLA ---
export function guardarHlaAPI(idPaciente, hlaData) {
  return fetchAPI(`/pacientes/${idPaciente}/hla`, {
    method: "POST",
    body: JSON.stringify(hlaData),
  });
}

// --- Crossmatch ---
export function guardarCrossmatchAPI(idPaciente, crossmatchData) {
  return fetchAPI(`/pacientes/${idPaciente}/crossmatch`, {
    method: "POST",
    body: JSON.stringify(crossmatchData),
  });
}

// --- Usuarios ---
export function getUsuariosAPI() {
  return fetchAPI("/usuarios");
}

export function deleteUsuarioAPI(id) {
  return fetchAPI(`/usuarios/${id}`, { method: "DELETE" });
}

export function updateUsuarioAPI(id, datosUsuario) {
  return fetchAPI(`/usuarios/${id}`, {
    method: "PUT",
    body: JSON.stringify(datosUsuario),
  });
}

export function changePasswordAPI(datosPassword) {
  return fetchAPI("/auth/change-password", {
    method: "POST",
    body: JSON.stringify(datosPassword),
  });
}

// --- DSA ---
export function getAnticuerposDsaAPI(idDsa) {
  return fetchAPI(`/anticuerpos/dsa/${idDsa}`);
}

// --- Anticuerpos ---
export function updateAnticuerpoAPI(idAnticuerpo, anticuerpoData) {
  return fetchAPI(`/anticuerpos/${idAnticuerpo}`, {
    method: "PUT",
    body: JSON.stringify(anticuerpoData),
  });
}

// --- INFORMES ---
export function getInformeDsaAPI(dni) {
  return fetchAPI(`/informes/dsa/buscar?dni=${dni}`);
}

export function guardarObservacionDsaAPI(idDsa, observaciones) {
  const payload = { observaciones: observaciones };
  return fetchAPI(`/informes/dsa/${idDsa}/observaciones`, {
    method: "PUT",
    body: JSON.stringify(payload),
  });
}

export function getInformeFamiliarAPI(dni) {
  return fetchAPI(`/informes/familiar/buscar?dni=${dni}`);
}

export function getInformeHlaAPI(dni) {
  return fetchAPI(`/informes/hla/buscar?dni=${dni}`);
}

export function guardarObservacionHlaAPI(idHla, observaciones) {
  const payload = { observaciones: observaciones };
  return fetchAPI(`/informes/hla/${idHla}/observaciones`, {
    method: "PUT",
    body: JSON.stringify(payload),
  });
}

export function guardarObservacionFamiliarAPI(idHlaReferencia, observaciones) {
  // Llama a: PUT /api/informes/familiar/{idHlaReferencia}/observaciones
  const payload = { observaciones: observaciones };
  return fetchAPI(`/informes/familiar/${idHlaReferencia}/observaciones`, {
    method: 'PUT',
    body: JSON.stringify(payload)
  });
}

// --- Crossmatch Virtual ---

/**
 * Obtiene los resultados DSA (anticuerpos) de un paciente (receptor).
 * Endpoint: GET /api/crossmatch/paciente/{patientIdentifier}/dsa
 */
export function getDsaResultadosByPacienteId(patientIdentifier) {
  // CORREGIDO: Usamos la variable 'patientIdentifier' que recibe la función
  return fetchAPI(`/crossmatch/paciente/${patientIdentifier}/dsa`);
}

/**
 * Obtiene la tipificación HLA de un Donante por ID.
 * Endpoint: GET /api/crossmatch/donante/{donorId}/hla
 */
export function getHlaByDonanteId(donorId) {
  // CORREGIDO: Usamos Path Variable (como espera el Controller) en lugar de Query Param
  return fetchAPI(`/crossmatch/donante/${donorId}/hla`);
}

// Agrega esto en api.js
export async function downloadPdfAPI(url, filename) {
  // CORRECCIÓN: Usamos la función getToken() importada de auth.js
  // para asegurarnos de usar la clave correcta ("authToken")
  const token = getToken(); 
  
  if (!token) {
    throw new Error("No hay token de autenticación. Inicie sesión nuevamente.");
  }

  const response = await fetch(url, {
    method: "GET",
    headers: {
      "Authorization": `Bearer ${token}` 
    }
  });

  if (!response.ok) {
    // Intentamos leer el error del backend si existe
    const errorText = await response.text();
    throw new Error(`Error al descargar PDF (${response.status}): ${errorText}`);
  }

  const blob = await response.blob();
  const blobUrl = window.URL.createObjectURL(blob);
  
  const a = document.createElement("a");
  a.href = blobUrl;
  a.download = filename;
  document.body.appendChild(a);
  a.click();
  
  document.body.removeChild(a);
  window.URL.revokeObjectURL(blobUrl);
}