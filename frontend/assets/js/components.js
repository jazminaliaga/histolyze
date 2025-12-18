// Función para cargar componentes HTML
export async function loadComponent(url, selector) {
  try {
    const res = await fetch(url);
    const html = await res.text();
    if (selector === "body") {
      document.body.insertAdjacentHTML("beforeend", html);
    } else {
      const el = document.querySelector(selector);
      if (el) el.innerHTML = html;
    }
  } catch (err) {
    console.error("Error cargando componente:", url, err);
  }
}
