package org.example.gestionsolicitudes.Service;

import jakarta.persistence.EntityNotFoundException;
import org.example.gestionsolicitudes.Dtos.HistorialSolicitudesRequestDTO;
import org.example.gestionsolicitudes.Dtos.HistorialSolicitudesResponseDTO;
import org.example.gestionsolicitudes.Mapper.HistorialSolicitudesMapper;
import org.example.gestionsolicitudes.Model.HistorialSolicitud;
import org.example.gestionsolicitudes.Model.Solicitud;
import org.example.gestionsolicitudes.Repository.HistorialSolicitudesRepository;
import org.example.gestionsolicitudes.Repository.SolicitudRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("HistorialSolicitudesService — Tests unitarios")
class HistorialSolicitudesServiceTest {

    @Mock private HistorialSolicitudesRepository historialRepository;
    @Mock private SolicitudRepository solicitudRepository;
    @Mock private HistorialSolicitudesMapper historialMapper;

    @InjectMocks private HistorialSolicitudesService historialService;

    private Solicitud solicitud;
    private HistorialSolicitud historial;
    private HistorialSolicitudesRequestDTO requestDTO;
    private HistorialSolicitudesResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        solicitud = Solicitud.builder()
                .idSolicitud(1L)
                .descripcion("Solicitud test")
                .build();

        historial = HistorialSolicitud.builder()
                .idHistorial(10L)
                .solicitud(solicitud)
                .accionRealizada("CREADA")
                .build();

        requestDTO = new HistorialSolicitudesRequestDTO();
        requestDTO.setIdSolicitud(1L);

        responseDTO = new HistorialSolicitudesResponseDTO();
    }

    // 🔹 REGISTRAR ACCIÓN
    @Nested
    @DisplayName("Registrar acción")
    class RegistrarTests {

        @Test
        @DisplayName("Debe registrar historial correctamente")
        void registrar_exitoso() {

            when(solicitudRepository.findById(1L)).thenReturn(Optional.of(solicitud));
            when(historialMapper.aEntidad(requestDTO, solicitud)).thenReturn(historial);
            when(historialRepository.save(historial)).thenReturn(historial);
            when(historialMapper.toResponse(historial)).thenReturn(responseDTO);

            HistorialSolicitudesResponseDTO res =
                    historialService.registrarAccion(requestDTO);

            assertThat(res).isNotNull();
            verify(historialRepository).save(historial);
        }

        @Test
        @DisplayName("Debe fallar si la solicitud no existe")
        void registrar_solicitudNoExiste() {

            when(solicitudRepository.findById(1L)).thenReturn(Optional.empty());

            assertThatThrownBy(() ->
                    historialService.registrarAccion(requestDTO))
                    .isInstanceOf(EntityNotFoundException.class);
        }
    }

    // 🔹 OBTENER HISTORIAL
    @Nested
    @DisplayName("Obtener historial")
    class ObtenerTests {

        @Test
        @DisplayName("Debe retornar lista de historial")
        void obtener_exitoso() {

            List<HistorialSolicitud> lista = List.of(historial);

            when(historialRepository.findBySolicitudIdSolicitud(1L))
                    .thenReturn(lista);
            when(historialMapper.aResponseList(lista))
                    .thenReturn(List.of(responseDTO));

            List<HistorialSolicitudesResponseDTO> res =
                    historialService.obtenerHistorialPorSolicitud(1L);

            assertThat(res).hasSize(1);
        }

        @Test
        @DisplayName("Debe retornar lista vacía")
        void obtener_vacio() {

            when(historialRepository.findBySolicitudIdSolicitud(1L))
                    .thenReturn(List.of());
            when(historialMapper.aResponseList(List.of()))
                    .thenReturn(List.of());

            List<HistorialSolicitudesResponseDTO> res =
                    historialService.obtenerHistorialPorSolicitud(1L);

            assertThat(res).isEmpty();
        }
    }
}