package com.deporshop.service;

import com.deporshop.exception.CredencialesInvalidasException;
import com.deporshop.exception.EmailYaRegistradoException;
import com.deporshop.model.Usuario;
import com.deporshop.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UsuarioService usuarioService;

    @Test
    void registrar_conEmailNuevo_hasheaLaPasswordYGuarda() {
        when(usuarioRepository.existsByEmail("juan@example.com")).thenReturn(false);
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));

        Usuario resultado = usuarioService.registrar("Juan", "juan@example.com", "secret123");

        assertThat(resultado.nombre).isEqualTo("Juan");
        assertThat(resultado.email).isEqualTo("juan@example.com");
        assertThat(resultado.password).isNotEqualTo("secret123"); // nunca se guarda en texto plano
        assertThat(new BCryptPasswordEncoder().matches("secret123", resultado.password)).isTrue();
    }

    @Test
    void registrar_conEmailYaRegistrado_lanzaExcepcion() {
        when(usuarioRepository.existsByEmail("juan@example.com")).thenReturn(true);

        assertThatThrownBy(() -> usuarioService.registrar("Juan", "juan@example.com", "secret123"))
            .isInstanceOf(EmailYaRegistradoException.class);
    }

    @Test
    void autenticar_conCredencialesCorrectas_devuelveElUsuario() {
        Usuario usuario = new Usuario();
        usuario.email = "juan@example.com";
        usuario.password = new BCryptPasswordEncoder().encode("secret123");
        when(usuarioRepository.findByEmail("juan@example.com")).thenReturn(Optional.of(usuario));

        Usuario resultado = usuarioService.autenticar("juan@example.com", "secret123");

        assertThat(resultado).isEqualTo(usuario);
    }

    @Test
    void autenticar_conPasswordIncorrecta_lanzaCredencialesInvalidas() {
        Usuario usuario = new Usuario();
        usuario.email = "juan@example.com";
        usuario.password = new BCryptPasswordEncoder().encode("secret123");
        when(usuarioRepository.findByEmail("juan@example.com")).thenReturn(Optional.of(usuario));

        assertThatThrownBy(() -> usuarioService.autenticar("juan@example.com", "wrong-password"))
            .isInstanceOf(CredencialesInvalidasException.class);
    }

    @Test
    void autenticar_conEmailInexistente_lanzaCredencialesInvalidas() {
        when(usuarioRepository.findByEmail("nadie@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> usuarioService.autenticar("nadie@example.com", "cualquiera"))
            .isInstanceOf(CredencialesInvalidasException.class);
    }
}
