package org.example.gestionsolicitudes.Dtos;

import lombok.Data;
import org.example.gestionsolicitudes.Model.EstadoSolicitud;

@Data
public class ResumenSolicitudResponseDTO {
    private Long idSolicitud;
    private EstadoSolicitud estadoSolicitud;
    private String resumenGenerado;
}
