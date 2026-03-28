package org.example.gestionsolicitudes.Controller;

import org.example.gestionsolicitudes.Dtos.HistorialSolicitudesRequestDTO;
import org.example.gestionsolicitudes.Dtos.HistorialSolicitudesResponseDTO;
import org.example.gestionsolicitudes.Service.HistorialSolicitudesService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(("historial-solicitudes"))
public class HistorialSolicitudesController {

    public HistorialSolicitudesService historialSolicitudesService;

    @PostMapping("/registrar-accion")
    public ResponseEntity<HistorialSolicitudesResponseDTO> registrarAccion(@RequestBody HistorialSolicitudesRequestDTO dto) {
        HistorialSolicitudesResponseDTO response = historialSolicitudesService.registrarAccion(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/solicitud/{idSolicitud}")
    public ResponseEntity<List<HistorialSolicitudesResponseDTO>> obtenerHistorialPorSolicitud(
            @PathVariable Long idSolicitud) {
        List<HistorialSolicitudesResponseDTO> historial = historialSolicitudesService.obtenerHistorialPorSolicitud(idSolicitud);
        return ResponseEntity.ok(historial);
    }

}
