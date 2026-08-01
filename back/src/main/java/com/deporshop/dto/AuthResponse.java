package com.deporshop.dto;

import lombok.Data;

@Data
public class AuthResponse {
    public String token;
    public UsuarioResponse usuario;

    public AuthResponse() {
    }

    public AuthResponse(String token, UsuarioResponse usuario) {
        this.token = token;
        this.usuario = usuario;
    }
}
