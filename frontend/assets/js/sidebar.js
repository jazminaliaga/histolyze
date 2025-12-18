export function initSidebarLinks() {
  const links = document.querySelectorAll("#sidebar .nav-link[data-module]");
  
  document.querySelectorAll("#sidebar .collapse").forEach(el => new bootstrap.Collapse(el, { toggle: false }));

  links.forEach(link => {
    link.addEventListener("click", (e) => {
      e.preventDefault();
      
      links.forEach(l => l.classList.remove("active"));
      link.classList.add("active");

      const moduleName = link.getAttribute("data-module");
      loadModuleAndInit(moduleName);
    });
  });
}

export async function loadModuleAndInit(moduleName, id = null) {
  const appContainer = document.querySelector("#app");
  if (!appContainer) {
    console.error("Error Crítico: El contenedor principal #app no fue encontrado.");
    return;
  }

  try {
    const htmlPath = `views/${moduleName}.html`;
    const response = await fetch(htmlPath);
    
    if (!response.ok) {
      throw new Error(`No se pudo encontrar el archivo de la vista: ${htmlPath} (Estado: ${response.status})`);
    }
    appContainer.innerHTML = await response.text();

    switch (moduleName) {
      case "pacientes_alta":
      case "pacientes_historial": {
        const { initPacienteModule } = await import('./pacientes.js');
        initPacienteModule();
        break;
      }
      case "crear_usuarios": {
        const { initUsuarioModule } = await import('./usuarios.js');
        initUsuarioModule();
        break;
      }
      case "perfil_usuario": {
        const { initPerfilModule } = await import('./perfil.js');
        initPerfilModule();
        break;
      }
      case "pacientes_detalle": {
        const { initPacienteDetalleModule } = await import('./pacientes_detalle.js');
        initPacienteDetalleModule(id);

        break;
      }
      case "carga_hla": {
        const { initHlaModule } = await import('./hla.js');
        initHlaModule();
        break;
      }
      case "carga_crossmatch_panel": {
        const { initCrossmatchModule } = await import('./crossmatch_panel.js');
        initCrossmatchModule();
        break;
      }
      case "carga_dsa": {
        const { initCargaDsaModule } = await import('./carga_dsa.js');
        initCargaDsaModule();
        break;
      }
      case "crossmatch_virtual": {
        const { initCrossmatchModule } = await import('./crossmatch_virtual.js');
        initCrossmatchModule();
        break;
      }
      case "informe_dsa": {
        const { initInformeDsaModule } = await import('./informes.js');
        initInformeDsaModule();
        break;
      }
      case "informe_familiar": {
        const { initInformeFamiliarModule } = await import('./informes.js'); 
        initInformeFamiliarModule();
        break;
      }
      case "informe_hla": {
        const { initInformeHlaModule } = await import('./informes.js');
        initInformeHlaModule();
        break;
      }
    }
  } catch (err) {
    console.error(`Error al cargar el módulo '${moduleName}':`, err);
    appContainer.innerHTML = `<div class="alert alert-danger">Error al cargar la sección. Revisa la consola para más detalles.</div>`;
  }
}

export function resetToHomeView() {
    const appContainer = document.querySelector("#app");
    if (appContainer) {
        // Vuelve a poner el contenido inicial en el área principal
        appContainer.innerHTML = `
            <div class="text-center">
                <img src="assets/img/logo-nombre.png" alt="Logo Histolyze" style="width: 400px; margin-top: 15vh;">
            </div>
        `;
    }

    // Quita la clase 'active' de todos los enlaces del sidebar
    const sidebarLinks = document.querySelectorAll("#sidebar .nav-link");
    sidebarLinks.forEach(link => link.classList.remove("active"));
}