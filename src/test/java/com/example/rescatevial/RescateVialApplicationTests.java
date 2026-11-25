package com.example.rescatevial;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
        "spring.autoconfigure.exclude=org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration",
        "jwt.secreto=secreto-exclusivo-de-pruebas-sin-datos-reales",
        "jwt.expiracion-horas=1"
})
class RescateVialApplicationTests {
    @org.springframework.test.context.bean.override.mockito.MockitoBean
    org.springframework.jdbc.core.JdbcTemplate jdbc;

    @Test
    void contextLoads() {
    }

}
