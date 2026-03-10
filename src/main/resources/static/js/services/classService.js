class ClassService {
    constructor(apiClient, endpoints) {
        this.apiClient = apiClient;
        this.endpoints = endpoints;
    }

    async obtenerClasesPorFecha(studioId, fecha) {
        const params = new URLSearchParams({
            studioId,
            fecha
        });
        return this.apiClient.get(`${this.endpoints.CLASSES}?${params.toString()}`);
    }

    async obtenerClasePorId(id) {
        return this.apiClient.get(`${this.endpoints.CLASSES}/${id}`);
    }

    async guardarClase(data) {
        return this.apiClient.post(this.endpoints.CLASSES, data);
    }
}

window.ClassService = ClassService;
