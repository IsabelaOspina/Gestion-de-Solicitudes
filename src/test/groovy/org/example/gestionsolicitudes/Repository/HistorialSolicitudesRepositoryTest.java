package org.example.gestionsolicitudes.Repository;

import org.example.gestionsolicitudes.Model.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("HistorialSolicitudesRepository — Tests de persistencia")
class HistorialSolicitudesRepositoryTest {

    @Autowired private HistorialSolicitudesRepository historialRepository;
    @Autowired private SolicitudRepository solicitudRepository;
    @Autowired private UsuarioRepository usuarioRepository;

    private Usuario usuario;
    private Solicitud solicitud;

    @BeforeEach
    void setUp() {
        usuario = Usuario.builder()
                .nombreUsuario("Usuario Test")
                .correoElectronico("test@correo.com")
                .password("123456")
                .rol(Rol.ESTUDIANTE)
                .activo(true)
                .build();

        usuario = usuarioRepository.save(usuario);

        solicitud = Solicitud.builder()
                .descripcion("Solicitud test")
                .canalOrigen(CanalOrigen.CORREO_ELECTRONICO)
                .tipoSolicitud(TipoSolicitud.CONSULTA_ACADEMICA)
                .estadoSolicitud(EstadoSolicitud.REGISTRADA)
                .nivelPrioridad(NivelPrioridad.MEDIA)
                .solicitante(usuario)
                .build();

        solicitud = solicitudRepository.save(solicitud);
    }

    private HistorialSolicitud crearHistorial(String accion) {
        return historialRepository.save(
                HistorialSolicitud.builder()
                        .solicitud(solicitud)
                        .fechaHora(LocalDateTime.now())
                        .accionRealizada(accion)
                        .observaciones("Observación test")
                        .build()
        );
    }

    @Nested
    @DisplayName("CRUD básico")
    class CrudTests {

        @Test
        @DisplayName("Guardar historial correctamente")
        void guardarHistorial() {
            HistorialSolicitud historial = crearHistorial("CREADA");

            assertThat(historial.getIdHistorial()).isNotNull();
            assertThat(historial.getAccionRealizada()).isEqualTo("CREADA");
            assertThat(historial.getSolicitud().getIdSolicitud())
                    .isEqualTo(solicitud.getIdSolicitud());
        }
    }

    @Nested
    @DisplayName("Consultas personalizadas")
    class QueryTests {

        @Test
        @DisplayName("findBySolicitudIdSolicitud retorna historial asociado")
        void findBySolicitudIdSolicitud() {
            crearHistorial("CREADA");
            crearHistorial("ACTUALIZADA");

            List<HistorialSolicitud> resultados =
                    historialRepository.findBySolicitudIdSolicitud(solicitud.getIdSolicitud());

            assertThat(resultados).hasSize(2);
            assertThat(resultados)
                    .extracting(HistorialSolicitud::getAccionRealizada)
                    .containsExactlyInAnyOrder("CREADA", "ACTUALIZADA");
        }

        @Test
        @DisplayName("findBySolicitudIdSolicitud retorna vacío si no existe")
        void findBySolicitudIdSolicitudVacio() {
            List<HistorialSolicitud> resultados =
                    historialRepository.findBySolicitudIdSolicitud(999L);

            assertThat(resultados).isEmpty();
        }
    }
}