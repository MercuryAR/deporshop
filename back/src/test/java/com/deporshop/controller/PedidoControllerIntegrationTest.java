package com.deporshop.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * El pedido se crea a partir de los items que manda el cliente (no del carrito de
 * sesión), así que este test no depende de cookies de sesión compartidas entre
 * requests - solo del token JWT, igual que lo haría el frontend real.
 */
@SpringBootTest
@AutoConfigureMockMvc
class PedidoControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String registrarYObtenerToken(String email) throws Exception {
        Map<String, String> registro = Map.of("nombre", "Comprador Test", "email", email, "password", "secret123");
        MvcResult result = mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registro)))
            .andExpect(status().isCreated())
            .andReturn();
        return objectMapper.readTree(result.getResponse().getContentAsString()).get("token").asText();
    }

    @Test
    void crearPedido_conItemsValidos_devuelve201YRecalculaElPrecioReal() throws Exception {
        String token = registrarYObtenerToken("pedido-" + UUID.randomUUID() + "@example.com");

        Map<String, Object> request = Map.of("items", java.util.List.of(
            Map.of("productoId", 1, "cantidad", 2)
        ));

        mockMvc.perform(post("/api/pedidos")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.estado").value("CONFIRMADO"))
            .andExpect(jsonPath("$.items", hasSize(1)))
            .andExpect(jsonPath("$.items[0].cantidad").value(2))
            .andExpect(jsonPath("$.items[0].productoId").value(1))
            .andExpect(jsonPath("$.total").isNumber());
    }

    @Test
    void crearPedido_yLuegoConsultarMisPedidos_loMuestraEnElHistorial() throws Exception {
        String email = "historial-" + UUID.randomUUID() + "@example.com";
        String token = registrarYObtenerToken(email);

        Map<String, Object> request = Map.of("items", java.util.List.of(
            Map.of("productoId", 1, "cantidad", 1)
        ));
        mockMvc.perform(post("/api/pedidos")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated());

        mockMvc.perform(get("/api/pedidos/mis-pedidos").header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].usuario.email").value(email));
    }

    @Test
    void crearPedido_conProductoInexistente_devuelve404() throws Exception {
        String token = registrarYObtenerToken("sinproducto-" + UUID.randomUUID() + "@example.com");

        Map<String, Object> request = Map.of("items", java.util.List.of(
            Map.of("productoId", 999999, "cantidad", 1)
        ));

        mockMvc.perform(post("/api/pedidos")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isNotFound());
    }

    @Test
    void crearPedido_conListaVacia_devuelve400() throws Exception {
        String token = registrarYObtenerToken("vacio-" + UUID.randomUUID() + "@example.com");

        Map<String, Object> request = Map.of("items", java.util.List.of());

        mockMvc.perform(post("/api/pedidos")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void crearPedido_sinToken_esRechazado() throws Exception {
        Map<String, Object> request = Map.of("items", java.util.List.of(Map.of("productoId", 1, "cantidad", 1)));

        mockMvc.perform(post("/api/pedidos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().is4xxClientError());
    }

    @Test
    void misPedidos_sinToken_esRechazado() throws Exception {
        mockMvc.perform(get("/api/pedidos/mis-pedidos"))
            .andExpect(status().is4xxClientError());
    }
}
