class ThemeService {
    constructor(apiClient, endpoints) {
        this.apiClient = apiClient;
        this.endpoints = endpoints;
    }

    async obtenerTema(studioId) {
        return this.apiClient.get(`${this.endpoints.THEMES}/${studioId}`);
    }
}

window.ThemeService = ThemeService;
