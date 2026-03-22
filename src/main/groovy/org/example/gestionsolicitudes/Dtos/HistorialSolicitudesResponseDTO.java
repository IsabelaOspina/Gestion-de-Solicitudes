package org.example.gestionsolicitudes.Dtos;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class HistorialSolicitudesResponseDTO {
    private Long idHistorial;
    private LocalDateTime fechaHora;
    private String accionRealizada;
    private String observaciones;

    //Datos solicitud
    private Long solicitudId;
    private String descripcionSolicitud;
}
