package com.deporshop.dto;

import com.deporshop.model.Usuario;
import lombok.Data;

@Data
public class UsuarioResponse {
    public Long id;
    public String nombre;
    public String email;

    public UsuarioResponse() {
    }

    public UsuarioResponse(Long id, String nombre, String email) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
    }

    public static UsuarioResponse desde(Usuario usuario) {
        return new UsuarioResponse(usuario.id, usuario.nombre, usuario.email);
    }
}
