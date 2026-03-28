package org.example.gestionsolicitudes.Service;

import jakarta.persistence.EntityNotFoundException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.example.gestionsolicitudes.Dtos.HistorialSolicitudesRequestDTO;
import org.example.gestionsolicitudes.Dtos.HistorialSolicitudesResponseDTO;
import org.example.gestionsolicitudes.Mapper.HistorialSolicitudesMapper;
import org.example.gestionsolicitudes.Model.HistorialSolicitud;
import org.example.gestionsolicitudes.Model.Solicitud;
import org.example.gestionsolicitudes.Repository.HistorialSolicitudesRepository;
import org.example.gestionsolicitudes.Repository.SolicitudRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Getter

public class HistorialSolicitudesService {

    private final HistorialSolicitudesRepository historialRepository;

    private final SolicitudRepository solicitudRepository;

    private final HistorialSolicitudesMapper historialMapper;

    //RF-06
    public HistorialSolicitudesResponseDTO registrarAccion(HistorialSolicitudesRequestDTO dto) {
        Solicitud solicitud = solicitudRepository.findById(dto.getIdSolicitud())
                .orElseThrow(() -> new EntityNotFoundException("Solicitud no encontrada"));

        HistorialSolicitud historial = historialMapper.aEntidad(dto, solicitud);
        historial = historialRepository.save(historial);

        return historialMapper.toResponse(historial);
    }

    public List<HistorialSolicitudesResponseDTO> obtenerHistorialPorSolicitud(Long idSolicitud) {
        List<HistorialSolicitud> historialList = historialRepository.findBySolicitudIdSolicitud(idSolicitud);
        return historialMapper.aResponseList(historialList);
    }
}
