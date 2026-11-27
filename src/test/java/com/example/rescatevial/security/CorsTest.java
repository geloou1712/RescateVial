package com.example.rescatevial.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = {
        "spring.config.import=",
        "spring.autoconfigure.exclude=org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration",
        "jwt.secreto=secreto-exclusivo-de-pruebas-cors",
        "app.cors.origenes=https://rescatevialfr.vercel.app"
})
class CorsTest {
    @MockitoBean JdbcTemplate jdbc;
    @Autowired WebApplicationContext context;
    private MockMvc mvc;

    @BeforeEach void preparar() {
        mvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test void permiteLoginDesdeProduccionSinToken() throws Exception {
        mvc.perform(options("/api/auth/login")
                .header("Origin", "https://rescatevialfr.vercel.app")
                .header("Access-Control-Request-Method", "POST")
                .header("Access-Control-Request-Headers", "content-type"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "https://rescatevialfr.vercel.app"));
    }

    @Test void permitePreflightConAuthorizationEnRutaProtegida() throws Exception {
        mvc.perform(options("/api/tipos-incidente")
                .header("Origin", "https://rescatevialfr.vercel.app")
                .header("Access-Control-Request-Method", "GET")
                .header("Access-Control-Request-Headers", "authorization"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "https://rescatevialfr.vercel.app"));
    }

    @Test void rechazaOrigenAjenoAunqueSeParezcaAlPermitido() throws Exception {
        mvc.perform(options("/api/auth/login")
                .header("Origin", "https://rescatevialfr.vercel.app.example.invalid")
                .header("Access-Control-Request-Method", "POST"))
                .andExpect(status().isForbidden())
                .andExpect(header().doesNotExist("Access-Control-Allow-Origin"));
    }
}
