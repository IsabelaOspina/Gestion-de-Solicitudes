package org.example.gestionsolicitudes.Dtos;

import lombok.Data;
import org.example.gestionsolicitudes.Model.NivelPrioridad;
import org.example.gestionsolicitudes.Model.TipoSolicitud;

@Data
public class SuguerenciaCalificacionResponseDTO {
    private TipoSolicitud tipoSolicitudSugerido;
    private NivelPrioridad prioridadSugerida;
}
