package com.deporshop.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

class JwtTokenProviderTest {

    // Base64-encoded, 256+ bits, solo para test - no es el secreto real de la app.
    private static final String TEST_SECRET =
        "q+LjVWuSp+DnlYfOq3v3Yl+s98TlKKEdEz4KfYTEVltHJUIm6MdxDQTD8c27yyQl6goHDmzHDqmAE87aFbWzfA==";

    private JwtTokenProvider jwtTokenProvider;

    @BeforeEach
    void setUp() {
        jwtTokenProvider = new JwtTokenProvider();
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtSecret", TEST_SECRET);
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtExpirationMs", 86_400_000L);
    }

    @Test
    void generarToken_devuelveUnTokenNoVacio() {
        String token = jwtTokenProvider.generarToken("juan@example.com");

        assertThat(token).isNotBlank();
    }

    @Test
    void obtenerEmailDeToken_devuelveElEmailUsadoParaGenerarlo() {
        String token = jwtTokenProvider.generarToken("juan@example.com");

        String email = jwtTokenProvider.obtenerEmailDeToken(token);

        assertThat(email).isEqualTo("juan@example.com");
    }

    @Test
    void validarToken_conTokenValido_devuelveTrue() {
        String token = jwtTokenProvider.generarToken("juan@example.com");

        assertThat(jwtTokenProvider.validarToken(token)).isTrue();
    }

    @Test
    void validarToken_conTokenMalformado_devuelveFalse() {
        assertThat(jwtTokenProvider.validarToken("esto-no-es-un-jwt")).isFalse();
    }

    @Test
    void validarToken_conTokenExpirado_devuelveFalse() {
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtExpirationMs", -1000L);

        String tokenExpirado = jwtTokenProvider.generarToken("juan@example.com");

        assertThat(jwtTokenProvider.validarToken(tokenExpirado)).isFalse();
    }

    @Test
    void validarToken_firmadoConOtraClave_devuelveFalse() {
        String token = jwtTokenProvider.generarToken("juan@example.com");

        JwtTokenProvider otroProvider = new JwtTokenProvider();
        // Otra clave Base64 válida de 256 bits, distinta a TEST_SECRET.
        ReflectionTestUtils.setField(otroProvider, "jwtSecret",
            "ZmFrZS1zZWNyZXQtcGFyYS10ZXN0LWNvbi1vdHJhLWNsYXZlLWRpc3RpbnRhLTMy");
        ReflectionTestUtils.setField(otroProvider, "jwtExpirationMs", 86_400_000L);

        assertThat(otroProvider.validarToken(token)).isFalse();
    }
}
