package com.avicola.monolith.iam.dto;

import java.util.UUID;

public class LoginResponse {
    private String token;
    private String type = "Bearer";
    private UUID id;
    private String nombreCompleto;
    private String email;
    private String rol;

    public LoginResponse() {}

    public LoginResponse(String token, UUID id, String nombreCompleto, String email, String rol) {
        this.token = token;
        this.id = id;
        this.nombreCompleto = nombreCompleto;
        this.email = email;
        this.rol = rol;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getNombreCompleto() { return nombreCompleto; }
    public void setNombreCompleto(String nombreCompleto) { this.nombreCompleto = nombreCompleto; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }
}
