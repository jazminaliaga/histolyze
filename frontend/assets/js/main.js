import { getUsuarioLogueado } from "./auth.js";
import { loadComponent } from "./components.js";
import { initSidebarLinks } from "./sidebar.js";
import { initNavbar } from "./navbar.js";
import { initModal } from "./modal.js";
import { initLoader } from "./loader.js";

// --- Verificar login ---
const usuario = getUsuarioLogueado();
if (!usuario && !window.location.href.includes("login.html")) {
  window.location.href = "login.html";
}

// --- Inicialización al cargar DOM ---
document.addEventListener("DOMContentLoaded", async () => {
  await Promise.all([
    loadComponent("components/navbar.html", "#navbar"),
    loadComponent("components/sidebar.html", "#sidebar"),
    loadComponent("components/modal.html", "body"),
    loadComponent("components/loader.html", "body"),
    loadComponent("components/alerts.html", "body")
  ]);

  initSidebarLinks();
  initNavbar();
  initModal();
  initLoader();
});
