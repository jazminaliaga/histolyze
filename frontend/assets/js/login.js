import { login } from "./auth.js";

const form = document.getElementById("loginForm");

form.addEventListener("submit", async (e) => { // La clave es 'async'
  e.preventDefault();
  const dni = form.dni.value;
  const password = form.password.value;

  const loginExitoso = await login(dni, password); // La clave es 'await'

  if (loginExitoso) {
    window.location.href = "index.html";
  } else {
    alert("DNI o contraseña incorrectos");
  }
});