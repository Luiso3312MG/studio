class AuthService {
    constructor(apiClient, endpoints) {
        this.apiClient = apiClient;
        this.endpoints = endpoints;
    }

    async login(username, password) {
        return this.apiClient.post(this.endpoints.LOGIN, {
            username,
            password
        });
    }

    async logout() {
        return this.apiClient.post("/auth/logout", {});
    }
}

window.AuthService = AuthService;
