package org.example.gestionsolicitudes.Mapper;

import lombok.experimental.UtilityClass;
import org.example.gestionsolicitudes.Dtos.CrearSolicitudRequestDTO;
import org.example.gestionsolicitudes.Dtos.PrioridadSolicitudRequestDTO;
import org.example.gestionsolicitudes.Dtos.SolicitudResponseDTO;
import org.example.gestionsolicitudes.Model.EstadoSolicitud;
import org.example.gestionsolicitudes.Model.HistorialSolicitud;
import org.example.gestionsolicitudes.Model.Solicitud;
import org.example.gestionsolicitudes.Model.Usuario;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class SolicitudMapper {

    // Entidad -> ResponseDTO
    public SolicitudResponseDTO aResponseDTO(Solicitud solicitud) {
        SolicitudResponseDTO dto = new SolicitudResponseDTO();
        dto.setIdSolicitud(solicitud.getIdSolicitud());
        dto.setDescripcion(solicitud.getDescripcion());
        dto.setFechaHoraRegistro(solicitud.getFechaHoraRegistro());
        dto.setCanalOrigen(solicitud.getCanalOrigen());
        dto.setTipoSolicitud(solicitud.getTipoSolicitud());
        dto.setEstadoSolicitud(solicitud.getEstadoSolicitud());
        dto.setFechaLimite(solicitud.getFechaLimite());
        dto.setNivelPrioridad(solicitud.getNivelPrioridad());
        dto.setJustificacionPrioridad(solicitud.getJustificacionPrioridad());

        // Datos solicitante
        if (solicitud.getSolicitante() != null) {
            dto.setSolicitanteId(solicitud.getSolicitante().getIdUsuario());
            dto.setNombreSolicitante(solicitud.getSolicitante().getNombreUsuario());
        }

        // Datos responsable
        if (solicitud.getResponsableAsignado() != null) {
            dto.setResponsableAsignadoId(solicitud.getResponsableAsignado().getIdUsuario());
            dto.setNombreResponsable(solicitud.getResponsableAsignado().getNombreUsuario());
        }

        return dto;
    }

    // CrearSolicitudRequestDTO -> Entidad
    public Solicitud aEntidad(CrearSolicitudRequestDTO dto, Usuario solicitante) {
        Solicitud solicitud = new Solicitud();
        solicitud.setDescripcion(dto.getDescripcion());
        solicitud.setTipoSolicitud(dto.getTipoSolicitud());
        solicitud.setCanalOrigen(dto.getCanalOrigen());
        solicitud.setSolicitante(solicitante);
        solicitud.setEstadoSolicitud(EstadoSolicitud.REGISTRADA);
        
        return solicitud;
    }

    // Actualizar prioridad desde DTO
    public void actualizarPrioridad(Solicitud solicitud, PrioridadSolicitudRequestDTO dto) {
        solicitud.setNivelPrioridad(dto.getPrioridad());
        solicitud.setJustificacionPrioridad(dto.getJustificacion());

        if (dto.getFechaLimite() != null) {
            solicitud.setFechaLimite(dto.getFechaLimite());
        }

        if (dto.getImpacto() != null) {
            HistorialSolicitud historial = HistorialSolicitud.builder()
                    .fechaHora(LocalDateTime.now())
                    .accionRealizada("Priorización")
                    .observaciones(dto.getImpacto())
                    .solicitud(solicitud)
                    .build();
            solicitud.getHistorial().add(historial);
        }
    }


}
