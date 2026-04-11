package org.example.gestionsolicitudes.Service;

import org.example.gestionsolicitudes.Dtos.*;
import org.example.gestionsolicitudes.Mapper.HistorialSolicitudesMapper;
import org.example.gestionsolicitudes.Mapper.SolicitudMapper;
import org.example.gestionsolicitudes.Model.*;
import org.example.gestionsolicitudes.Repository.HistorialSolicitudesRepository;
import org.example.gestionsolicitudes.Repository.SolicitudRepository;
import org.example.gestionsolicitudes.Repository.UsuarioRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("SolicitudService — Tests unitarios")
class SolicitudServiceTest {

    @Mock private SolicitudRepository solicitudRepository;
    @Mock private UsuarioService usuarioService;
    @Mock private HistorialSolicitudesRepository historialRepository;
    @Mock private HistorialSolicitudesMapper historialMapper;
    @Mock private SolicitudMapper solicitudMapper;
    @Mock private IAService iaService;
    @Mock private UsuarioRepository usuarioRepository;

    @InjectMocks private SolicitudService solicitudService;

    private Usuario estudiante;
    private Usuario admin;
    private Solicitud solicitud;

    @BeforeEach
    void setUp() {
        estudiante = Usuario.builder()
                .idUsuario(1L)
                .nombreUsuario("Estudiante")
                .rol(Rol.ESTUDIANTE)
                .activo(true)
                .build();

        admin = Usuario.builder()
                .idUsuario(2L)
                .nombreUsuario("Admin")
                .rol(Rol.ADMINISTRATIVO)
                .activo(true)
                .build();

        solicitud = Solicitud.builder()
                .idSolicitud(10L)
                .descripcion("Problema académico")
                .estadoSolicitud(EstadoSolicitud.REGISTRADA)
                .tipoSolicitud(TipoSolicitud.CONSULTA_ACADEMICA)
                .canalOrigen(CanalOrigen.CORREO_ELECTRONICO)
                .historial(new ArrayList<>())
                .build();
    }

    // REGISTRAR
    @Nested
    @DisplayName("Registrar solicitud")
    class RegistrarTests {

        @Test
        @DisplayName("Debe registrar correctamente")
        void registrar_exitoso() {

            CrearSolicitudRequestDTO dto = new CrearSolicitudRequestDTO();

            Authentication authentication = mock(Authentication.class);

            SecurityContext securityContext = mock(SecurityContext.class);

            SecurityContextHolder.setContext(securityContext);

            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getName()).thenReturn("estudiante1");

            when(usuarioRepository.findByNombreUsuario("estudiante1"))
                    .thenReturn(Optional.of(estudiante));

            when(solicitudMapper.aEntidad(dto, estudiante)).thenReturn(solicitud);
            when(solicitudRepository.save(any())).thenReturn(solicitud);
            when(solicitudMapper.aResponseDTO(any())).thenReturn(new SolicitudResponseDTO());

            SolicitudResponseDTO res =
                    solicitudService.registrarSolicitud(dto);

            assertThat(res).isNotNull();
            verify(solicitudRepository).save(any());
        }

        @Test
        @DisplayName("Debe fallar si es administrativo")
        void registrar_adminFalla() {

            CrearSolicitudRequestDTO dto = new CrearSolicitudRequestDTO();

            Authentication authentication = mock(Authentication.class);
            SecurityContext securityContext = mock(SecurityContext.class);

            SecurityContextHolder.setContext(securityContext);

            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getName()).thenReturn("admin1");

            when(usuarioRepository.findByNombreUsuario("admin1"))
                    .thenReturn(Optional.of(admin));

            assertThatThrownBy(() ->
                    solicitudService.registrarSolicitud(dto))
                    .isInstanceOf(IllegalStateException.class);
        }
    }

    // PRIORIZAR
    @Nested
    @DisplayName("Priorizar solicitud")
    class PriorizarTests {

        @Test
        @DisplayName("Debe priorizar con IA")
        void priorizar_conIA() {
            PrioridadSolicitudRequestDTO dto = new PrioridadSolicitudRequestDTO();
            dto.setUsarIA(true);

            when(solicitudRepository.findById(10L)).thenReturn(Optional.of(solicitud));
            when(iaService.sugerirPrioridad(any(), any(), any()))
                    .thenReturn("PRIORIDAD: ALTA JUSTIFICACION: urgente");
            when(solicitudRepository.save(any())).thenReturn(solicitud);
            when(solicitudMapper.aResponseDTO(any())).thenReturn(new SolicitudResponseDTO());

            SolicitudResponseDTO res =
                    solicitudService.priorizarSolicitud(10L, dto);

            assertThat(res).isNotNull();
            assertThat(solicitud.getNivelPrioridad()).isEqualTo(NivelPrioridad.ALTA);
        }

        @Test
        @DisplayName("Debe fallar si solicitud no existe")
        void priorizar_noExiste() {
            when(solicitudRepository.findById(10L)).thenReturn(Optional.empty());

            assertThatThrownBy(() ->
                    solicitudService.priorizarSolicitud(10L, new PrioridadSolicitudRequestDTO()))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    // ASIGNAR RESPONSABLE
    @Nested
    @DisplayName("Asignar responsable")
    class AsignarTests {

        @Test
        @DisplayName("Debe asignar correctamente")
        void asignar_exitoso() {
            when(solicitudRepository.findById(10L)).thenReturn(Optional.of(solicitud));
            when(usuarioService.obtenerUsuarioActivo(2L)).thenReturn(admin);
            when(solicitudMapper.aResponseDTO(any())).thenReturn(new SolicitudResponseDTO());

            SolicitudResponseDTO res =
                    solicitudService.asignarResponsable(10L, 2L);

            assertThat(res).isNotNull();
            assertThat(solicitud.getEstadoSolicitud()).isEqualTo(EstadoSolicitud.EN_ATENCION);
            verify(historialRepository).save(any());
        }

        @Test
        @DisplayName("Debe fallar si no es administrativo")
        void asignar_noAdmin() {
            when(solicitudRepository.findById(10L)).thenReturn(Optional.of(solicitud));
            when(usuarioService.obtenerUsuarioActivo(1L)).thenReturn(estudiante);

            assertThatThrownBy(() ->
                    solicitudService.asignarResponsable(10L, 1L))
                    .isInstanceOf(IllegalStateException.class);
        }
    }

    // CERRAR
    @Nested
    @DisplayName("Cerrar solicitud")
    class CerrarTests {

        @Test
        @DisplayName("Debe cerrar correctamente")
        void cerrar_exitoso() {
            solicitud.setEstadoSolicitud(EstadoSolicitud.ATENDIDA);

            when(solicitudRepository.findById(10L)).thenReturn(Optional.of(solicitud));
            when(solicitudRepository.save(any())).thenReturn(solicitud);
            when(solicitudMapper.aResponseDTO(any())).thenReturn(new SolicitudResponseDTO());

            SolicitudResponseDTO res =
                    solicitudService.cerrarSolicitud(10L, "Resuelto");

            assertThat(res).isNotNull();
            assertThat(solicitud.getEstadoSolicitud()).isEqualTo(EstadoSolicitud.CERRADA);
        }

        @Test
        @DisplayName("Debe fallar si no está atendida")
        void cerrar_estadoInvalido() {
            solicitud.setEstadoSolicitud(EstadoSolicitud.REGISTRADA);

            when(solicitudRepository.findById(10L)).thenReturn(Optional.of(solicitud));

            assertThatThrownBy(() ->
                    solicitudService.cerrarSolicitud(10L, "x"))
                    .isInstanceOf(IllegalStateException.class);
        }
    }

    // RESUMEN IA
    @Nested
    @DisplayName("Generar resumen")
    class ResumenTests {

        @Test
        @DisplayName("Debe generar resumen correctamente")
        void resumen_exitoso() {
            when(solicitudRepository.findById(10L)).thenReturn(Optional.of(solicitud));
            when(iaService.generarResumen(any())).thenReturn("Resumen generado");

            ResumenSolicitudResponseDTO res =
                    solicitudService.generarResumenSolicitud(10L);

            assertThat(res).isNotNull();
            assertThat(res.getResumenGenerado()).isEqualTo("Resumen generado");
        }

        @Test
        @DisplayName("Debe fallar si solicitud no existe")
        void resumen_noExiste() {
            when(solicitudRepository.findById(10L)).thenReturn(Optional.empty());

            assertThatThrownBy(() ->
                    solicitudService.generarResumenSolicitud(10L))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }
}