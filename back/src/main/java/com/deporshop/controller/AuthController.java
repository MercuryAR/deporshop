package com.deporshop.controller;

import com.deporshop.dto.AuthResponse;
import com.deporshop.dto.LoginRequest;
import com.deporshop.dto.RegisterRequest;
import com.deporshop.dto.UsuarioResponse;
import com.deporshop.model.Usuario;
import com.deporshop.security.JwtTokenProvider;
import com.deporshop.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> registrar(@Valid @RequestBody RegisterRequest request) {
        Usuario usuario = usuarioService.registrar(request.nombre, request.email, request.password);
        String token = jwtTokenProvider.generarToken(usuario.email);
        AuthResponse response = new AuthResponse(token, UsuarioResponse.desde(usuario));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        Usuario usuario = usuarioService.autenticar(request.email, request.password);
        String token = jwtTokenProvider.generarToken(usuario.email);
        AuthResponse response = new AuthResponse(token, UsuarioResponse.desde(usuario));
        return ResponseEntity.ok(response);
    }
}
