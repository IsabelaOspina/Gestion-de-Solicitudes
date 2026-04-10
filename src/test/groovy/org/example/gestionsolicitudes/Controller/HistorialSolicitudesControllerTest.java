package org.example.gestionsolicitudes.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.gestionsolicitudes.Dtos.HistorialSolicitudesRequestDTO;
import org.example.gestionsolicitudes.Dtos.HistorialSolicitudesResponseDTO;
import org.example.gestionsolicitudes.Service.HistorialSolicitudesService;
import org.example.gestionsolicitudes.config.JwtService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;


@WebMvcTest(HistorialSolicitudesController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@DisplayName("HistorialSolicitudesController — Tests de integración web")
class HistorialSolicitudesControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private HistorialSolicitudesService historialSolicitudesService;

    @MockBean
    private JwtService jwtService;


    private HistorialSolicitudesResponseDTO mockResponse() {

        HistorialSolicitudesResponseDTO dto = new HistorialSolicitudesResponseDTO();
        dto.setIdHistorial(1L);
        dto.setFechaHora(LocalDateTime.now());
        dto.setAccionRealizada("Asignación de responsable");
        dto.setObservaciones("Asignado correctamente");

        dto.setIdSolicitud(10L);
        dto.setDescripcionSolicitud("Solicitud de soporte");
        dto.setIdResponsable(5L);

        return dto;
    }

    @Test
    @DisplayName("201 — Registrar acción en historial")
    void registrarAccion_exitoso() throws Exception {

        HistorialSolicitudesRequestDTO request = new HistorialSolicitudesRequestDTO();
        request.setIdSolicitud(10L);
        request.setAccionRealizada("Asignación");
        request.setObservaciones("Asignado a responsable");

        when(historialSolicitudesService.registrarAccion(any()))
                .thenReturn(mockResponse());

        mockMvc.perform(post("/historial-solicitudes/registrar-accion")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accionRealizada").value("Asignación de responsable"))
                .andExpect(jsonPath("$.idSolicitud").value(10));
    }

    @Test
    @DisplayName("200 — Obtener historial por solicitud")
    void obtenerHistorialPorSolicitud() throws Exception {

        when(historialSolicitudesService.obtenerHistorialPorSolicitud(anyLong()))
                .thenReturn(List.of(mockResponse()));

        mockMvc.perform(get("/historial-solicitudes/solicitud/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].accionRealizada").value("Asignación de responsable"))
                .andExpect(jsonPath("$[0].idSolicitud").value(10));
    }
}