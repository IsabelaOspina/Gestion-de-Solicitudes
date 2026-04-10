package org.example.gestionsolicitudes.Service;

import org.example.gestionsolicitudes.Model.CanalOrigen;
import org.example.gestionsolicitudes.Model.TipoSolicitud;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = "gemini.api.key=${GEMINI_API_KEY}")
class IAServiceIntegrationTest {

    @Autowired
    private IAService iaService;

    @Test
    void generarResumen_IntegracionReal() {
        // Requiere GEMINI_API_KEY configurada
        String prompt = "Resume: El usuario no puede iniciar sesión desde ayer";

        String resultado = iaService.generarResumen(prompt);

        assertNotNull(resultado);
        assertFalse(resultado.isEmpty());
        System.out.println("Resumen generado: " + resultado);
    }

    @Test
    void sugerirPrioridad_IntegracionReal() {
        String resultado = iaService.sugerirPrioridad(
                "Sac caido, no puedo acceder a mis cursos en línea y tengo un examen mañana",
                TipoSolicitud.CONSULTA_ACADEMICA,
                CanalOrigen.CORREO_ELECTRONICO
        );

        assertNotNull(resultado);
        assertTrue(resultado.contains("PRIORIDAD:"));
        assertTrue(resultado.contains("ALTA") || resultado.contains("MEDIA") || resultado.contains("BAJA"));
        System.out.println("Prioridad sugerida:\n" + resultado);
    }
}