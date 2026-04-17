package org.example.gestionsolicitudes.Dtos;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

@Data
public class HistorialSolicitudesRequestDTO {

    @NotNull(message = "La fecha y hora de la acción es obligatoria")
    private LocalDateTime fechaHora;

    @NotBlank(message = "La acción realizada no puede estar vacía")
    @Size(max = 100, message = "La acción realizada no puede exceder los 100 caracteres")
    private String accionRealizada;

    @NotBlank(message = "Las observaciones no pueden estar vacías")
    @Size(max = 255, message = "Las observaciones no pueden exceder los 255 caracteres")
    private String observaciones;

    @NotNull(message = "El ID de la solicitud es obligatorio")
    private Long idSolicitud;
}