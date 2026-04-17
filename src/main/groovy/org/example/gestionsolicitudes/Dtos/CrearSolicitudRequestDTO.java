package org.example.gestionsolicitudes.Dtos;

import lombok.Data;
import org.example.gestionsolicitudes.Model.CanalOrigen;
import org.example.gestionsolicitudes.Model.TipoSolicitud;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Data
public class CrearSolicitudRequestDTO {

    @NotBlank(message = "La solicitud debe tener una descripción")
    @Size(max = 250, message = "Máximo 250 caracteres")
    private String descripcion;

    @NotNull(message = "El tipo de solicitud es obligatorio")
    private TipoSolicitud tipoSolicitud;

    @NotNull(message = "El canal de origen es obligatorio")
    private CanalOrigen canalOrigen;
}