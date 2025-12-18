import { loginAPI, registerAPI, getUsuariosAPI, deleteUsuarioAPI, updateUsuarioAPI, changePasswordAPI } from './api.js';

// --- LÓGICA DE LOGIN REAL CON BACKEND ---
export async function login(dni, password) {
  try {
    const data = await loginAPI(dni, password);
    if (data && data.token) {
      localStorage.setItem("authToken", data.token);
      localStorage.setItem("usuarioLogueado", JSON.stringify(data.usuario));
      return true;
    }
    return false;
  } catch (error) {
    console.error("Error en el login:", error);
    return false;
  }
}

export function logout() {
  localStorage.removeItem("usuarioLogueado");
  localStorage.removeItem("authToken");
  window.location.href = "login.html";
}

export function getUsuarioLogueado() {
  return JSON.parse(localStorage.getItem("usuarioLogueado") || "null");
}

export function getToken() {
  return localStorage.getItem("authToken");
}

// --- FUNCIONES DE GESTIÓN DE USUARIOS CONECTADAS AL BACKEND ---

export async function registrarUsuario(nuevoUsuario) {
    try {
        return await registerAPI(nuevoUsuario);
    } catch (err) {
        console.error("Error en registrarUsuario:", err);
        throw err;
    }
}

// --- FUNCIÓN ACTUALIZADA ---
export async function actualizarUsuario(id, datosActualizados) {
  try {
    const usuarioActualizado = await updateUsuarioAPI(id, datosActualizados);
    // Verificamos si el usuario editado es el que está logueado
    const usuarioLogueado = getUsuarioLogueado();
    if (usuarioLogueado && usuarioLogueado.idUsuario === id) {
        // Actualizamos también la información de la sesión actual
        usuarioLogueado.nombre = usuarioActualizado.nombre;
        usuarioLogueado.apellido = usuarioActualizado.apellido;
        localStorage.setItem("usuarioLogueado", JSON.stringify(usuarioLogueado));
    }
    return usuarioActualizado;
  } catch (err) {
    console.error("Error al actualizar usuario:", err);
    throw err;
  }
}

export async function eliminarUsuario(id) {
  try {
    return await deleteUsuarioAPI(id);
  } catch (err) {
    console.error("Error al eliminar usuario:", err);
    throw err;
  }
}

export async function getTodosLosUsuarios() {
    try {
        return await getUsuariosAPI();
    } catch(err) {
        console.error("Error obteniendo usuarios desde la API:", err);
        return [];
    }
}

// --- FUNCIÓN ACTUALIZADA ---
export async function cambiarContraseña(datosPassword) {
  try {
    // El objeto datosPassword debería contener el id del usuario y la nueva contraseña
    return await changePasswordAPI(datosPassword);
  } catch(err) {
    console.error("Error al cambiar la contraseña:", err);
    throw err;
  }
}