package org.example.gestionsolicitudes.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.gestionsolicitudes.Dtos.CrearSolicitudRequestDTO;
import org.example.gestionsolicitudes.Dtos.PrioridadSolicitudRequestDTO;
import org.example.gestionsolicitudes.Dtos.ResumenSolicitudResponseDTO;
import org.example.gestionsolicitudes.Dtos.SolicitudResponseDTO;
import org.example.gestionsolicitudes.Model.CanalOrigen;
import org.example.gestionsolicitudes.Model.EstadoSolicitud;
import org.example.gestionsolicitudes.Model.NivelPrioridad;
import org.example.gestionsolicitudes.Model.TipoSolicitud;
import org.example.gestionsolicitudes.Service.SolicitudService;
import org.example.gestionsolicitudes.Service.UsuarioService;
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
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@WebMvcTest(SolicitudController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@DisplayName("SolicitudController — Tests de integración web")
class SolicitudControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SolicitudService solicitudService;

    @MockBean
    private JwtService jwtService;


    private SolicitudResponseDTO mockResponse() {

        SolicitudResponseDTO resp = new SolicitudResponseDTO();

        resp.setIdSolicitud(1L);
        resp.setDescripcion("Solicitud de prueba");
        resp.setFechaHoraRegistro(LocalDateTime.now());
        resp.setCanalOrigen(CanalOrigen.SAC);
        resp.setTipoSolicitud(TipoSolicitud.HOMOLOGACION);
        resp.setEstadoSolicitud(EstadoSolicitud.REGISTRADA);
        resp.setFechaLimite(LocalDateTime.now().plusDays(3));
        resp.setNivelPrioridad(NivelPrioridad.MEDIA);
        resp.setJustificacionPrioridad("Prioridad normal");

        resp.setSolicitanteId(10L);
        resp.setNombreSolicitante("Juan Perez");

        resp.setResponsableAsignadoId(20L);
        resp.setNombreResponsable("Maria Lopez");

        return resp;
    }

    @Test
    @DisplayName("201 — Registrar solicitud exitosamente")
    void registrarSolicitud_exitoso() throws Exception {

        CrearSolicitudRequestDTO request = new CrearSolicitudRequestDTO();
        request.setDescripcion("Nueva solicitud de prueba");
        request.setTipoSolicitud(TipoSolicitud.HOMOLOGACION);
        request.setCanalOrigen(CanalOrigen.SAC);

        when(solicitudService.registrarSolicitud(any()))
                .thenReturn(mockResponse());

        mockMvc.perform(post("/solicitudes/registrar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.descripcion").value("Solicitud de prueba"));;
    }


    @Test
    @DisplayName("200 — Priorizar solicitud")
    void priorizarSolicitud_exitoso() throws Exception {


        PrioridadSolicitudRequestDTO request = new PrioridadSolicitudRequestDTO();
        request.setPrioridad(NivelPrioridad.ALTA);
        request.setUsarIA(false);

        when(solicitudService.priorizarSolicitud(anyLong(), any()))
                .thenReturn(mockResponse());

        mockMvc.perform(put("/solicitudes/priorizar/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print()) // opcional para debug
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nivelPrioridad").value("MEDIA"));
    }


    @Test
    @DisplayName("200 — Asignar responsable")
    void asignarResponsable_exitoso() throws Exception {

        when(solicitudService.asignarResponsable(anyLong(), anyLong()))
                .thenReturn(mockResponse());

        mockMvc.perform(post("/solicitudes/1/asignar/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.descripcion").value("Solicitud de prueba"));
    }


    @Test
    @DisplayName("200 — Consultar solicitudes por estado")
    void consultarPorEstado() throws Exception {

        List<SolicitudResponseDTO> lista = List.of(mockResponse());

        when(solicitudService.consultarPorEstado(any()))
                .thenReturn(lista);

        mockMvc.perform(get("/solicitudes/estado/REGISTRADA"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].descripcion").value("Solicitud de prueba"));
    }


    @Test
    @DisplayName("200 — Consultar solicitudes por prioridad")
    void consultarPorPrioridad() throws Exception {

        List<SolicitudResponseDTO> lista = List.of(mockResponse());

        when(solicitudService.consultarPorPrioridad(any()))
                .thenReturn(lista);

        mockMvc.perform(get("/solicitudes/prioridad/MEDIA"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].descripcion").value("Solicitud de prueba"));
    }


    @Test
    @DisplayName("200 — Cerrar solicitud")
    void cerrarSolicitud() throws Exception {

        when(solicitudService.cerrarSolicitud(anyLong(), anyString()))
                .thenReturn(mockResponse());

        mockMvc.perform(post("/solicitudes/cerrar/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("\"Solicitud finalizada\""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.descripcion").value("Solicitud de prueba"));
    }


    @Test
    @DisplayName("200 — Generar resumen de solicitud")
    void generarResumen() throws Exception {

        ResumenSolicitudResponseDTO resumen = new ResumenSolicitudResponseDTO();
        resumen.setIdSolicitud(1L);
        resumen.setEstadoSolicitud(EstadoSolicitud.CERRADA);
        resumen.setResumenGenerado("Resumen de solicitud");

        when(solicitudService.generarResumenSolicitud(anyLong()))
                .thenReturn(resumen);

        mockMvc.perform(get("/solicitudes/1/resumen"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idSolicitud").value(1))
                .andExpect(jsonPath("$.estadoSolicitud").value("CERRADA"))
                .andExpect(jsonPath("$.resumenGenerado").value("Resumen de solicitud"));

    }
}