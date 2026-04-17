package org.example.gestionsolicitudes.Dtos;

import lombok.Data;
import org.example.gestionsolicitudes.Model.NivelPrioridad;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;


@Data
public class PrioridadSolicitudRequestDTO {

    @NotNull(message = "El nivel de prioridad es obligatorio")
    private NivelPrioridad prioridad;

    @Size(max = 250, message = "El impacto no puede exceder los 250 caracteres")
    private String impacto;

    @Size(max = 250, message = "La justificación no puede exceder los 250 caracteres")
    private String justificacion;

    private Boolean usarIA;
}