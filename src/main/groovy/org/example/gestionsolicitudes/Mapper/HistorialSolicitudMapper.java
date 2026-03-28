package org.example.gestionsolicitudes.Mapper;

import org.example.gestionsolicitudes.Dtos.HistorialSolicitudesRequestDTO;
import org.example.gestionsolicitudes.Dtos.HistorialSolicitudesResponseDTO;
import org.example.gestionsolicitudes.Model.HistorialSolicitud;
import org.example.gestionsolicitudes.Model.Solicitud;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class HistorialSolicitudMapper {

    public HistorialSolicitud aEntidad(HistorialSolicitudesRequestDTO dto, Solicitud solicitud) {
        HistorialSolicitud historial = new HistorialSolicitud();
        historial.setFechaHora(dto.getFechaHora());
        historial.setAccionRealizada(dto.getAccionRealizada());
        historial.setObservaciones(dto.getObservaciones());
        historial.setSolicitud(solicitud);

        return historial;
    }

    public HistorialSolicitudesResponseDTO toResponse(HistorialSolicitud entity) {

        HistorialSolicitudesResponseDTO response = new HistorialSolicitudesResponseDTO();
        response.setIdHistorial(entity.getIdHistorial());
        response.setFechaHora(entity.getFechaHora());
        response.setAccionRealizada(entity.getAccionRealizada());
        response.setObservaciones(entity.getObservaciones());

        // Datos de la solicitud
        if (entity.getSolicitud() != null) {
            response.setIdSolicitud(entity.getSolicitud().getIdSolicitud());
            response.setDescripcionSolicitud(entity.getSolicitud().getDescripcion());
            if (entity.getSolicitud().getResponsableAsignado() != null) {
                response.setIdResponsable(entity.getSolicitud().getResponsableAsignado().getIdUsuario());
            }
        }

        return response;
    }

     public List<HistorialSolicitudesResponseDTO> aResponseList(List<HistorialSolicitud> entidades) {
        return entidades.stream()
                .map(this::toResponse)
                .toList();
    }

}
