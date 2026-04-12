package org.example.gestionsolicitudes.Controller;

import org.example.gestionsolicitudes.Dtos.HistorialSolicitudesRequestDTO;
import org.example.gestionsolicitudes.Dtos.HistorialSolicitudesResponseDTO;
import org.example.gestionsolicitudes.Service.HistorialSolicitudesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(("historial-solicitudes"))
public class HistorialSolicitudesController {

    @Autowired
    public HistorialSolicitudesService historialSolicitudesService;

    @GetMapping("/solicitud/{idSolicitud}")
    public ResponseEntity<List<HistorialSolicitudesResponseDTO>> obtenerHistorialPorSolicitud(
            @PathVariable("idSolicitud") Long idSolicitud) {
        List<HistorialSolicitudesResponseDTO> historial = historialSolicitudesService.obtenerHistorialPorSolicitud(idSolicitud);
        return ResponseEntity.ok(historial);
    }

}
