package com.deporshop.service;

import com.deporshop.exception.CredencialesInvalidasException;
import com.deporshop.exception.EmailYaRegistradoException;
import com.deporshop.model.Usuario;
import com.deporshop.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public Usuario registrar(String nombre, String email, String rawPassword) {
        if (usuarioRepository.existsByEmail(email)) {
            throw new EmailYaRegistradoException("El email ya está registrado");
        }
        Usuario usuario = new Usuario();
        usuario.nombre = nombre;
        usuario.email = email;
        usuario.password = passwordEncoder.encode(rawPassword);
        return usuarioRepository.save(usuario);
    }

    public Optional<Usuario> buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    public Usuario autenticar(String email, String rawPassword) {
        Usuario usuario = usuarioRepository.findByEmail(email)
            .orElseThrow(() -> new CredencialesInvalidasException("Credenciales inválidas"));
        if (!passwordEncoder.matches(rawPassword, usuario.password)) {
            throw new CredencialesInvalidasException("Credenciales inválidas");
        }
        return usuario;
    }
}
