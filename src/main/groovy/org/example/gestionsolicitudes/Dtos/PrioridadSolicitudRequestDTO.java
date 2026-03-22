package org.example.gestionsolicitudes.Dtos;
import lombok.Data;
import java.time.LocalDate;
public class PrioridadSolicitudRequestDTO {
    private NivelPrioridad prioridad;
    private String impacto;
    private LocalDate fechaLimite;
    private String justificacion;
}
