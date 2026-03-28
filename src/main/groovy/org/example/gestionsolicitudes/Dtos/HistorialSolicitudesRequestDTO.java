package org.example.gestionsolicitudes.Dtos;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class HistorialSolicitudesRequestDTO {
    private LocalDateTime fechaHora;
    private String accionRealizada;
    private String observaciones;
    private Long idSolicitud;
}
