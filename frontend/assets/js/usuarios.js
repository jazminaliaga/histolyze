import {
  registrarUsuario,
  getTodosLosUsuarios,
  eliminarUsuario,
  actualizarUsuario,
  getUsuarioLogueado,
} from "./auth.js";
import { showAlert } from "./alerts.js";
import { renderNavbarUsuario } from "./navbar.js";

let modalEditar = null;

async function renderUsuarios() {
  // La función ahora es async
  const usuarios = await getTodosLosUsuarios(); // Espera a que la API responda
  const tbody = document.querySelector("#usuariosTable tbody");
  if (!tbody) return;

  tbody.innerHTML = "";
  usuarios.forEach((user) => {
    const tr = document.createElement("tr");
    tr.innerHTML = `
            <td>${user.dni}</td>
            <td>${user.nombre}</td>
            <td>${user.apellido || ""}</td>
            <td>${user.role}</td> 
            <td class="text-end">
                <button class="btn btn-sm btn-outline-primary btn-editar" data-id="${
                  user.idUsuario
                }" data-dni="${user.dni}">Editar</button>
                <button class="btn btn-sm btn-outline-danger btn-eliminar" data-id="${
                  user.idUsuario
                }">Eliminar</button>
            </td>
        `;
    tbody.appendChild(tr);
  });
}

export function initUsuarioModule() {
  const usuario = getUsuarioLogueado();
  const container = document.getElementById("gestionUsuariosContainer");

  if (!usuario || usuario.role !== "ADMIN") {
    // Si el usuario no es admin o no está logueado
    if (container) {
      // Ocultamos el contenido y mostramos un mensaje de error
      container.innerHTML = `
                <div class="alert alert-danger" role="alert">
                    <h4 class="alert-heading">Acceso Denegado</h4>
                    <p>No tienes los permisos necesarios para acceder a esta sección.</p>
                </div>
            `;
    }
    return; // Detenemos la ejecución para no cargar el resto de funciones
  }

  const modalEl = document.getElementById("modalEditarUsuario");
  if (modalEl) {
    modalEditar = new bootstrap.Modal(modalEl);
  }
  initCrearUsuarioForm();
  initTablaUsuariosActions();
  renderUsuarios();
}

async function initCrearUsuarioForm() {
  // La función ahora es async
  const form = document.getElementById("formCrearUsuario");
  if (!form) return;

  form.addEventListener("submit", async (e) => {
    e.preventDefault();
    const nombre = document.getElementById("crear-nombre").value;
    const apellido = document.getElementById("crear-apellido").value;
    const dni = document.getElementById("crear-dni").value;
    const password = document.getElementById("crear-password").value;
    const role = document.getElementById("crear-rol").value;

    // Construimos el objeto con la estructura que el backend espera
    const nuevoUsuario = {
      nombre,
      apellido,
      dni,
      password,
      role,
    };
    try {
      await registrarUsuario(nuevoUsuario);
      form.reset();
      showAlert("Usuario creado correctamente", "success");
      await renderUsuarios(); // Refresca la tabla desde la API
    } catch (err) {
      showAlert(err.message || "Error al crear usuario", "danger");
    }
  });
}

function initTablaUsuariosActions() {
  const tabla = document.querySelector("#usuariosTable tbody");
  if (!tabla) return;

  // 1. Convertimos la función del listener en ASÍNCRONA con "async"
  tabla.addEventListener("click", async (e) => {
    if (e.target.classList.contains("btn-eliminar")) {
      const id = e.target.dataset.id;
      if (confirm(`¿Estás seguro de que deseas eliminar a este usuario?`)) {
        try {
          await eliminarUsuario(id);
          showAlert("Usuario eliminado con éxito", "success");
          await renderUsuarios(); // Refrescamos la tabla
        } catch (err) {
          showAlert(err.message || "Error al eliminar usuario", "danger");
        }
      }
    }

    if (e.target.classList.contains("btn-editar")) {
      const id = e.target.dataset.id;

      // 2. ESPERAMOS ("await") a que la lista de usuarios llegue de la API
      const usuarios = await getTodosLosUsuarios();

      // Ahora "usuarios" es un array real y .find() funcionará
      const usuario = usuarios.find((u) => u.idUsuario == id); // Usamos '==' para comparar string con número si es necesario

      if (usuario && modalEditar) {
        const form = document.getElementById("formEditarUsuario");
        // Guardamos el ID en el formulario para usarlo al guardar
        form.idUsuario.value = usuario.idUsuario;
        form.dni.value = usuario.dni;
        form.nombre.value = usuario.nombre;
        form.apellido.value = usuario.apellido;
        modalEditar.show();
      }
    }
  });

  // Listener para el formulario DENTRO del modal de edición
  const formEditar = document.getElementById("formEditarUsuario");
  if (formEditar) {
    // 1. Convertimos la función en ASÍNCRONA
    formEditar.addEventListener("submit", async (e) => {
      e.preventDefault();

      // 2. Leemos el ID y lo guardamos en una variable con el nombre correcto ("id")
      const id = formEditar.idUsuario.value;
      const datosActualizados = {
        nombre: formEditar.nombre.value,
        apellido: formEditar.apellido.value,
      };

      // 3. Usamos un bloque try/catch para manejar el éxito y el error
      try {
        // 4. ESPERAMOS (await) a que la API termine de actualizar
        await actualizarUsuario(id, datosActualizados);

        // Si la línea anterior no dio error, mostramos el éxito
        showAlert("Usuario actualizado con éxito", "success");
        modalEditar.hide();
        await renderUsuarios(); // Refrescamos la tabla
        renderNavbarUsuario(); // Refrescamos el navbar
      } catch (err) {
        // Si la API devuelve un error, lo mostramos sin salir de la app
        showAlert(err.message || "Error al actualizar usuario", "danger");
      }
    });
  }
}
