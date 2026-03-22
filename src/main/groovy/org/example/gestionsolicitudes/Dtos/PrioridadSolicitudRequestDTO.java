package org.example.gestionsolicitudes.Dtos;
import lombok.Data;
import org.example.gestionsolicitudes.Model.NivelPrioridad;

import java.time.LocalDateTime;

@Data
public class PrioridadSolicitudRequestDTO {
    private NivelPrioridad prioridad;
    private String impacto;
    private LocalDateTime fechaLimite;
    private String justificacion;
}
