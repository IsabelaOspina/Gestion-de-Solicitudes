package org.example.gestionsolicitudes.Dtos;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
public class AtenderSolicitudRequestDTO {

    @NotBlank(message = "La observación no puede estar vacía")
    @Size(max = 250, message = "La observación no puede exceder los 250 caracteres")
    private String observacion;
}