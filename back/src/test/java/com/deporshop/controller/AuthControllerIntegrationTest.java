package com.deporshop.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test de integración full-stack (contexto Spring real, H2 en memoria, filtro JWT real).
 * Cubre el contrato exacto que consume el frontend: forma de request/response, status codes.
 */
@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String emailUnico() {
        return "test-" + UUID.randomUUID() + "@example.com";
    }

    @Test
    void registrar_conDatosValidos_devuelve201ConTokenYUsuario() throws Exception {
        String email = emailUnico();
        Map<String, String> body = Map.of("nombre", "Juan Perez", "email", email, "password", "secret123");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.token").isNotEmpty())
            .andExpect(jsonPath("$.usuario.email").value(email))
            .andExpect(jsonPath("$.usuario.nombre").value("Juan Perez"))
            .andExpect(jsonPath("$.usuario.password").doesNotExist());
    }

    @Test
    void registrar_conEmailDuplicado_devuelve400() throws Exception {
        String email = emailUnico();
        Map<String, String> body = Map.of("nombre", "Juan Perez", "email", email, "password", "secret123");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)))
            .andExpect(status().isCreated());

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void registrar_conPasswordCorta_devuelve400ConFieldErrors() throws Exception {
        Map<String, String> body = Map.of("nombre", "Juan", "email", emailUnico(), "password", "123");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.fieldErrors.password").exists());
    }

    @Test
    void login_conCredencialesCorrectas_devuelve200ConToken() throws Exception {
        String email = emailUnico();
        Map<String, String> registro = Map.of("nombre", "Juan Perez", "email", email, "password", "secret123");
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registro)))
            .andExpect(status().isCreated());

        Map<String, String> login = Map.of("email", email, "password", "secret123");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(login)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token").isNotEmpty())
            .andExpect(jsonPath("$.usuario.email").value(email));
    }

    @Test
    void login_conPasswordIncorrecta_devuelve401() throws Exception {
        String email = emailUnico();
        Map<String, String> registro = Map.of("nombre", "Juan Perez", "email", email, "password", "secret123");
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registro)))
            .andExpect(status().isCreated());

        Map<String, String> login = Map.of("email", email, "password", "password-incorrecta");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(login)))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void login_conEmailInexistente_devuelve401() throws Exception {
        Map<String, String> login = Map.of("email", emailUnico(), "password", "cualquiera");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(login)))
            .andExpect(status().isUnauthorized());
    }
}
