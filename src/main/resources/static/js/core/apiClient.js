class ApiClient {
    constructor(baseUrl) {
        this.baseUrl = baseUrl;
    }

    async request(endpoint, options = {}) {
        const url = this.baseUrl + endpoint;
        const config = {
            method: options.method || "GET",
            headers: {
                "Content-Type": "application/json",
                ...(options.headers || {})
            },
            credentials: "include"
        };

        if (options.body && config.method !== "GET" && config.method !== "HEAD") {
            config.body = JSON.stringify(options.body);
        }

        const response = await fetch(url, config);
        if (!response.ok) {
            throw new Error(`HTTP Error ${response.status}`);
        }
        const contentType = response.headers.get("content-type") || "";
        if (contentType.includes("application/json")) {
            return await response.json();
        }
        return await response.text();
    }

    get(endpoint) {
        return this.request(endpoint, { method: "GET" });
    }
    post(endpoint, body) {
        return this.request(endpoint, { method: "POST", body });
    }
    put(endpoint, body) {
        return this.request(endpoint, { method: "PUT", body });
    }
    delete(endpoint) {
        return this.request(endpoint, { method: "DELETE" });
    }
}

window.ApiClient = ApiClient;
