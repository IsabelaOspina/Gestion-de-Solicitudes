package org.example.gestionsolicitudes.Controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.gestionsolicitudes.Dtos.*;
import org.example.gestionsolicitudes.Model.*;
import org.example.gestionsolicitudes.Service.SolicitudService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@Validated
@RequestMapping("/solicitudes")
@RequiredArgsConstructor
public class SolicitudController {

    private final SolicitudService solicitudService;

    @PostMapping("/registrar")
    public ResponseEntity<SolicitudResponseDTO> registrarSolicitud(
            @Valid @RequestBody CrearSolicitudRequestDTO dto){

        SolicitudResponseDTO nuevaSolicitud =
                solicitudService.registrarSolicitud(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(nuevaSolicitud);
    }

    @PutMapping("/priorizar/{idSolicitud}")
    public ResponseEntity<SolicitudResponseDTO> priorizarSolicitud( @PathVariable("idSolicitud") Long idSolicitud,@Valid @RequestBody PrioridadSolicitudRequestDTO dto) {
         SolicitudResponseDTO solicitudPriorizada = solicitudService.priorizarSolicitud(idSolicitud, dto);
         return ResponseEntity.ok(solicitudPriorizada);
    }

    @PutMapping("/{idSolicitud}/asignar/{idResponsable}")
    public ResponseEntity<SolicitudResponseDTO> asignarResponsable(@PathVariable("idSolicitud") Long idSolicitud, @PathVariable("idResponsable") Long idResponsable) {
        SolicitudResponseDTO solicitudAsignada = solicitudService.asignarResponsable(idSolicitud, idResponsable);
        return ResponseEntity.ok(solicitudAsignada);
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<SolicitudResponseDTO>> consultarPorEstado(@PathVariable("estado")EstadoSolicitud estado) {
        return ResponseEntity.ok(solicitudService.consultarPorEstado(estado));
    }

    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<List<SolicitudResponseDTO>> consultarPorTipo(@PathVariable("tipo") TipoSolicitud tipo) {
        return ResponseEntity.ok(solicitudService.consultarPorTipo(tipo));
    }

    @GetMapping("/prioridad/{prioridad}")
    public ResponseEntity<List<SolicitudResponseDTO>> consultarPorPrioridad(@PathVariable("prioridad") NivelPrioridad prioridad) {
        return ResponseEntity.ok(solicitudService.consultarPorPrioridad(prioridad));
    }

    @GetMapping("/responsable/{idResponsable}")
    public ResponseEntity<List<SolicitudResponseDTO>> consultarPorResponsable(@PathVariable("idResponsable") Long idResponsable) {
        return ResponseEntity.ok(solicitudService.consultarPorResponsable(idResponsable));
    }

    @GetMapping("/rango")
    public ResponseEntity<List<SolicitudResponseDTO>> consultarPorRangoFechas(
            @RequestParam LocalDateTime desde,
            @RequestParam LocalDateTime hasta) {
        return ResponseEntity.ok(solicitudService.consultarPorRangoFechas(desde, hasta));
    }

    @GetMapping("/estado-tipo")
    public ResponseEntity<List<SolicitudResponseDTO>> consultarPorEstadoYTipo(
            @RequestParam EstadoSolicitud estado,
            @RequestParam TipoSolicitud tipo) {
        return ResponseEntity.ok(solicitudService.consultarPorEstadoYTipo(estado, tipo));
    }

    @GetMapping("/solicitante/{idSolicitante}")
    public ResponseEntity<List<SolicitudResponseDTO>> consultarPorSolicitante(@PathVariable("idSolicitante") Long idSolicitante) {
        return ResponseEntity.ok(solicitudService.consultarPorSolicitante(idSolicitante));
    }

    @PutMapping("/cerrar/{idSolicitud}")
    public ResponseEntity<SolicitudResponseDTO> cerrarSolicitud(
            @PathVariable("idSolicitud") Long idSolicitud,
            @Valid @RequestBody CerrarSolicitudRequestDTO dto) {
        return ResponseEntity.ok(
                solicitudService.cerrarSolicitud(idSolicitud, dto.getObservacionCierre()));
    }

    @GetMapping("/{idSolicitud}/resumen")
    public ResponseEntity<ResumenSolicitudResponseDTO> generarResumen(@PathVariable("idSolicitud") Long idSolicitud) {
        ResumenSolicitudResponseDTO response = solicitudService.generarResumenSolicitud(idSolicitud);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/atender/{idSolicitud}")
    public ResponseEntity<SolicitudResponseDTO> atenderSolicitud(
            @PathVariable("idSolicitud") Long idSolicitud,
            @Valid @RequestBody AtenderSolicitudRequestDTO dto) {

        SolicitudResponseDTO response =
                solicitudService.atenderSolicitud(idSolicitud, dto.getObservacion());

        return ResponseEntity.ok(response);
    }

}
