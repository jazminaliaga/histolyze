import { getUsuarioLogueado, cambiarContraseña, logout } from './auth.js';
import { showAlert } from './alerts.js';

// La lógica ahora está dentro de una función exportable
export function initPerfilModule() {
    const usuario = getUsuarioLogueado();

    if (!usuario) {
        window.location.href = 'login.html';
        return;
    }

    // Cargar datos del usuario en la vista
    document.getElementById('datoNombre').textContent = usuario.nombre;
    document.getElementById('datoApellido').textContent = usuario.apellido;
    document.getElementById('datoDni').textContent = usuario.dni;

    const btnEditar = document.getElementById('btnEditarPerfil');
    const btnCancelar = document.getElementById('btnCancelarEdicion');
    const formEditar = document.getElementById('formEditarPerfil');
    const usuarioDatos = document.getElementById('usuarioDatos');

    btnEditar.addEventListener('click', () => {
        formEditar.classList.remove('d-none');
        usuarioDatos.classList.add('d-none');
        btnEditar.classList.add('d-none');
    });

    btnCancelar.addEventListener('click', () => {
        formEditar.classList.add('d-none');
        usuarioDatos.classList.remove('d-none');
        btnEditar.classList.remove('d-none');
        formEditar.reset();
        formEditar.classList.remove('was-validated');
    });

    formEditar.addEventListener('submit', (e) => {
        e.preventDefault();
        e.stopPropagation();

        if (!formEditar.checkValidity()) {
            formEditar.classList.add('was-validated');
            return;
        }

        const nuevaContraseña = e.target.password.value;
        const confirmarContraseña = e.target.confirmPassword.value;

        if (nuevaContraseña !== confirmarContraseña) {
            showAlert('Las contraseñas no coinciden.', 'warning', 4000);
            return;
        }

        const exito = cambiarContraseña(usuario.dni, nuevaContraseña);

        if (exito) {
            alert("Contraseña cambiada con éxito. Serás redirigido al login para iniciar sesión de nuevo.");
            logout();
        } else {
            showAlert('Hubo un error inesperado al cambiar la contraseña.', 'danger', 4000);
        }
    });
}