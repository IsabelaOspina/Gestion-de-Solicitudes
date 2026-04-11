package org.example.gestionsolicitudes.Service;

import org.example.gestionsolicitudes.Model.CanalOrigen;
import org.example.gestionsolicitudes.Model.Solicitud;
import org.example.gestionsolicitudes.Model.TipoSolicitud;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = "gemini.api.key=${GEMINI_API_KEY}")
class IAServiceTest {

    @Autowired
    private IAService iaService;

    @Test
    void generarResumen_IntegracionReal() {

        // Crear solicitud de prueba
        Solicitud solicitud = new Solicitud();
        solicitud.setDescripcion("Necesito urgentemente un certificado de estudio del semestre pasado para un trámite académico.");
        solicitud.setTipoSolicitud(TipoSolicitud.CONSULTA_ACADEMICA);
        solicitud.setCanalOrigen(CanalOrigen.CORREO_ELECTRONICO);

        String resultado = iaService.generarResumen(solicitud);

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