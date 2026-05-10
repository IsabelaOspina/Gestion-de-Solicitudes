package org.example.gestionsolicitudes.Dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CerrarSolicitudRequestDTO {
    @NotBlank(message = "La observación de cierre es obligatoria")
    private String observacionCierre;
}