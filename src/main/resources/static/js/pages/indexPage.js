async function initPage() {
    try {
        const studioId = 1;
        // Example: use today's date; adjust as needed
        const fecha = new Date().toISOString().split('T')[0];

        const tema = await app.themeService.obtenerTema(studioId);
        aplicarTema(tema);

        const clases = await app.classService.obtenerClasesPorFecha(studioId, fecha);
        renderClases(clases);
    } catch (error) {
        console.error("Error inicializando página:", error);
    }
}

function aplicarTema(tema) {
    if (!tema) return;
    document.documentElement.style.setProperty('--primary-color', tema.primary_color || '#000000');
    document.documentElement.style.setProperty('--secondary-color', tema.secondary_color || '#ffffff');
}

function renderClases(clases) {
    // TODO: implement UI rendering
    console.log('Clases cargadas:', clases);
}

document.addEventListener('DOMContentLoaded', initPage);
