package org.example.gestionsolicitudes.Dtos;
import lombok.Data;
import org.example.gestionsolicitudes.Model.CanalOrigen;
import org.example.gestionsolicitudes.Model.TipoSolicitud;

@Data
public class CrearSolicitudRequestDTO {
    private String descripcion;
    private TipoSolicitud tipoSolicitud;
    private CanalOrigen canalOrigen;
    private Long solicitanteId;
}
