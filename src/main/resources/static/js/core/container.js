(() => {
    const apiClient = new ApiClient(window.APP_CONFIG.API_BASE);
    const endpoints = window.APP_CONFIG.ENDPOINTS;

    const classService = new ClassService(apiClient, endpoints);
    const themeService = new ThemeService(apiClient, endpoints);
    const authService = new AuthService(apiClient, endpoints);

    window.app = {
        apiClient,
        classService,
        themeService,
        authService
    };
})();
