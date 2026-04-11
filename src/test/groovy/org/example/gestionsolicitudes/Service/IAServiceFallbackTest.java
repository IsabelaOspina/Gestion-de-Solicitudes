package org.example.gestionsolicitudes.Service;

import org.example.gestionsolicitudes.Model.EstadoSolicitud;
import org.example.gestionsolicitudes.Model.Solicitud;
import org.example.gestionsolicitudes.Model.TipoSolicitud;
import org.example.gestionsolicitudes.Model.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(MockitoExtension.class)
@DisplayName("IAServiceFallback — Tests unitarios")
class IAServiceFallbackTest {

    private IAServiceFallback fallback;

    @BeforeEach
    void setUp() {
        fallback = new IAServiceFallback();
    }

    //
    // TESTS DE RESUMEN
    @Nested
    @DisplayName("Generar resumen con datos")
    class GenerarResumenTests {

        @Test
        @DisplayName("Debe generar resumen con datos básicos")
        void generarResumen_basico() {

            Solicitud solicitud = Solicitud.builder()
                    .idSolicitud(1L)
                    .descripcion("Solicitud de prueba")
                    .estadoSolicitud(EstadoSolicitud.REGISTRADA)
                    .tipoSolicitud(TipoSolicitud.CONSULTA_ACADEMICA)
                    .historial(new ArrayList<>())
                    .build();

            String resultado = fallback.generarResumenConDatos(solicitud, new Exception());

            assertThat(resultado).contains("Solicitud #1");
            assertThat(resultado).contains("Descripción: Solicitud de prueba");
            assertThat(resultado).contains("Tipo: CONSULTA_ACADEMICA");
            assertThat(resultado).contains("[Fallback: generado con datos locales]");
        }

        @Test
        @DisplayName("Debe incluir responsable si existe")
        void generarResumen_conResponsable() {

            Usuario usuario = Usuario.builder()
                    .nombreUsuario("Admin")
                    .build();

            Solicitud solicitud = Solicitud.builder()
                    .idSolicitud(2L)
                    .descripcion("Otra solicitud")
                    .estadoSolicitud(EstadoSolicitud.REGISTRADA)
                    .tipoSolicitud(TipoSolicitud.HOMOLOGACION)
                    .responsableAsignado(usuario)
                    .historial(new ArrayList<>())
                    .build();

            String resultado = fallback.generarResumenConDatos(solicitud, new Exception());

            assertThat(resultado).contains("Responsable: Admin");
        }
    }

    // TESTS DE PRIORIDAD
    @Nested
    @DisplayName("Sugerir prioridad fallback")
    class PrioridadFallbackTests {

        @Test
        @DisplayName("Debe asignar ALTA para cancelación")
        void prioridad_cancelacion() {

            String resultado = fallback.sugerirPrioridadFallback(
                    "Quiero cancelar materia",
                    TipoSolicitud.CANCELACION_ASIGNATURA
            );

            assertThat(resultado).contains("PRIORIDAD: ALTA");
        }

        @Test
        @DisplayName("Debe asignar MEDIA para homologación")
        void prioridad_homologacion() {

            String resultado = fallback.sugerirPrioridadFallback(
                    "Solicito homologación",
                    TipoSolicitud.HOMOLOGACION
            );

            assertThat(resultado).contains("PRIORIDAD: MEDIA");
        }

        @Test
        @DisplayName("Debe asignar BAJA para consulta académica")
        void prioridad_consulta() {

            String resultado = fallback.sugerirPrioridadFallback(
                    "Tengo una duda",
                    TipoSolicitud.CONSULTA_ACADEMICA
            );

            assertThat(resultado).contains("PRIORIDAD: BAJA");
        }

        @Test
        @DisplayName("Debe subir a ALTA si es urgente")
        void prioridad_urgente() {

            String resultado = fallback.sugerirPrioridadFallback(
                    "Necesito esto urgente",
                    TipoSolicitud.CONSULTA_ACADEMICA
            );

            assertThat(resultado).contains("PRIORIDAD: ALTA");
            assertThat(resultado).contains("urgencia");
        }
    }
}