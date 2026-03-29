package org.example.gestionsolicitudes.Controller;

import org.example.gestionsolicitudes.Dtos.CrearSolicitudRequestDTO;
import org.example.gestionsolicitudes.Dtos.PrioridadSolicitudRequestDTO;
import org.example.gestionsolicitudes.Dtos.SolicitudResponseDTO;
import org.example.gestionsolicitudes.Model.*;
import org.example.gestionsolicitudes.Service.SolicitudService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/solicitudes")
public class SolicitudController {

    public SolicitudService solicitudService;

    @PostMapping("/registrar/{idSolicitante}")
    public ResponseEntity<SolicitudResponseDTO> registrarSolicitud(@RequestBody CrearSolicitudRequestDTO dto, @PathVariable Long idSolicitante) {
        SolicitudResponseDTO nuevaSolicitud = solicitudService.registrarSolicitud(dto, idSolicitante);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevaSolicitud);
    }

    @PutMapping("/priorizar/{idSolicitud}")
    public ResponseEntity<SolicitudResponseDTO> priorizarSolicitud(@PathVariable Long idSolicitud, @RequestBody PrioridadSolicitudRequestDTO dto) {
         SolicitudResponseDTO solicitudPriorizada = solicitudService.priorizarSolicitud(idSolicitud, dto);
         return ResponseEntity.ok(solicitudPriorizada);
    }

    @PostMapping("/{idSolicitud}/asignar/{idResponsable}")
    public ResponseEntity<SolicitudResponseDTO> asignarResponsable(@PathVariable Long idSolicitud, @PathVariable Long idResponsable) {
        SolicitudResponseDTO solicitudAsignada = solicitudService.asignarResponsable(idSolicitud, idResponsable);
        return ResponseEntity.ok(solicitudAsignada);
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<SolicitudResponseDTO>> consultarPorEstado(@PathVariable EstadoSolicitud estado) {
        return ResponseEntity.ok(solicitudService.consultarPorEstado(estado));
    }

    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<List<SolicitudResponseDTO>> consultarPorTipo(@PathVariable TipoSolicitud tipo) {
        return ResponseEntity.ok(solicitudService.consultarPorTipo(tipo));
    }

    @GetMapping("/prioridad/{prioridad}")
    public ResponseEntity<List<SolicitudResponseDTO>> consultarPorPrioridad(@PathVariable NivelPrioridad prioridad) {
        return ResponseEntity.ok(solicitudService.consultarPorPrioridad(prioridad));
    }

    @GetMapping("/responsable/{idResponsable}")
    public ResponseEntity<List<SolicitudResponseDTO>> consultarPorResponsable(@PathVariable Long idResponsable) {
        Usuario responsable = solicitudService.getUsuarioService().obtenerPorId(idResponsable);
        return ResponseEntity.ok(solicitudService.consultarPorResponsable(responsable));
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
    public ResponseEntity<List<SolicitudResponseDTO>> consultarPorSolicitante(@PathVariable Long idSolicitante) {
        Usuario solicitante = solicitudService.getUsuarioService().obtenerPorId(idSolicitante);
        return ResponseEntity.ok(solicitudService.consultarPorSolicitante(solicitante));
    }

    @PostMapping("/cerrar/{idSolicitud}")
    public ResponseEntity<SolicitudResponseDTO> cerrarSolicitud(@PathVariable Long idSolicitud, @RequestBody String observacionCierre) {
        SolicitudResponseDTO solicitudCerrada = solicitudService.cerrarSolicitud(idSolicitud, observacionCierre);
        return ResponseEntity.ok(solicitudCerrada);
    }

}
