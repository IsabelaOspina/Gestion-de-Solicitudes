package org.example.gestionsolicitudes.Dtos;
import lombok.Data;
@Data
public class CrearSolicitudRequestDTO {
    private String descripcion;
    private TipoSolicitud tipoSolicitud;
    private CanalOrigen canalOrigen;
    private Long solicitanteId;
}
