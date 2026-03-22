package org.example.gestionsolicitudes.Dtos;

import lombok.Data;
import org.example.gestionsolicitudes.Model.CanalOrigen;
import org.example.gestionsolicitudes.Model.EstadoSolicitud;
import org.example.gestionsolicitudes.Model.NivelPrioridad;
import org.example.gestionsolicitudes.Model.TipoSolicitud;

import java.time.LocalDateTime;

@Data
public class SolicitudResponseDTO {
    private Long idSolicitud;
    private String descripcion;
    private LocalDateTime fechaHoraRegistro;
    private CanalOrigen canalOrigen;
    private TipoSolicitud tipoSolicitud;
    private EstadoSolicitud estadoSolicitud;
    private LocalDateTime fechaLimite;
    private NivelPrioridad nivelPrioridad;
    private String justificacionPrioridad;

    // Datos solicitante
    private Long solicitanteId;
    private String nombreSolicitante;

    // Datos responsable
    private Long responsableAsignadoId;
    private String nombreResponsable;
}

