package org.example.gestionsolicitudes.Service;


import lombok.RequiredArgsConstructor;
import org.example.gestionsolicitudes.Dtos.CrearSolicitudRequestDTO;
import org.example.gestionsolicitudes.Dtos.PrioridadSolicitudRequestDTO;
import org.example.gestionsolicitudes.Dtos.SolicitudResponseDTO;
import org.example.gestionsolicitudes.Mapper.SolicitudMapper;
import org.example.gestionsolicitudes.Model.*;
import org.example.gestionsolicitudes.Repository.SolicitudRepository;
import org.example.gestionsolicitudes.Repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor

public class SolicitudService {
    private final SolicitudRepository solicitudRepository;
    private final UsuarioRepository usuarioRepository;

    // RF-01: Registrar solicitud
    public SolicitudResponseDTO registrarSolicitud(CrearSolicitudRequestDTO dto, Long idSolicitante) {
        Usuario solicitante = usuarioRepository.findById(idSolicitante)
                .orElseThrow(() -> new IllegalArgumentException("Solicitante no encontrado"));

        Solicitud solicitud = SolicitudMapper.aEntidad(dto, solicitante);
        solicitud.setFechaHoraRegistro(LocalDateTime.now());

        // Historial inicial
        HistorialSolicitud historial = HistorialSolicitud.builder()
                .fechaHora(LocalDateTime.now())
                .accionRealizada("Registro de solicitud")
                .observaciones("Solicitud registrada por " + solicitante.getNombreUsuario())
                .solicitud(solicitud)
                .build();
        solicitud.getHistorial().add(historial);

        return SolicitudMapper.aResponseDTO(solicitudRepository.save(solicitud));
    }

    // RF-03: Priorización
    public SolicitudResponseDTO priorizarSolicitud(Long idSolicitud, PrioridadSolicitudRequestDTO dto) {
        Solicitud solicitud = solicitudRepository.findById(idSolicitud)
                .orElseThrow(() -> new IllegalArgumentException("Solicitud no encontrada"));

        SolicitudMapper.actualizarPrioridad(solicitud, dto);

        return SolicitudMapper.aResponseDTO(solicitudRepository.save(solicitud));
    }

    // RF-05: Asignación de responsable
    public SolicitudResponseDTO asignarResponsable(Long idSolicitud, Long idResponsable) {
        Solicitud solicitud = solicitudRepository.findById(idSolicitud)
                .orElseThrow(() -> new IllegalArgumentException("Solicitud no encontrada"));

        Usuario responsable = usuarioRepository.findById(idResponsable)
                .orElseThrow(() -> new IllegalArgumentException("Responsable no encontrado"));

        if (!responsable.getActivo()) {
            throw new IllegalStateException("El responsable no está activo");
        }

        solicitud.setResponsableAsignado(responsable);
        solicitud.setEstadoSolicitud(EstadoSolicitud.EN_ATENCION);

        HistorialSolicitud historial = HistorialSolicitud.builder()
                .fechaHora(LocalDateTime.now())
                .accionRealizada("Asignación de responsable")
                .observaciones("Asignado a " + responsable.getNombreUsuario())
                .solicitud(solicitud)
                .build();
        solicitud.getHistorial().add(historial);

        return SolicitudMapper.aResponseDTO(solicitudRepository.save(solicitud));
    }

    // RF-07: Consultas simples (estado, tipo, prioridad, responsable)
    public List<SolicitudResponseDTO> consultarPorEstado(EstadoSolicitud estado) {
        return solicitudRepository.findByEstadoSolicitud(estado)
                .stream()
                .map(SolicitudMapper::aResponseDTO)
                .toList();
    }

    public List<SolicitudResponseDTO> consultarPorTipo(TipoSolicitud tipo) {
        return solicitudRepository.findByTipoSolicitud(tipo)
                .stream()
                .map(SolicitudMapper::aResponseDTO)
                .toList();
    }

    public List<SolicitudResponseDTO> consultarPorPrioridad(NivelPrioridad prioridad) {
        return solicitudRepository.findByNivelPrioridad(prioridad)
                .stream()
                .map(SolicitudMapper::aResponseDTO)
                .toList();
    }

    public List<SolicitudResponseDTO> consultarPorResponsable(Usuario responsable) {
        return solicitudRepository.findByResponsableAsignado(responsable)
                .stream()
                .map(SolicitudMapper::aResponseDTO)
                .toList();
    }

    // RF-08: Cierre de solicitud
    public SolicitudResponseDTO cerrarSolicitud(Long idSolicitud, String observacionCierre) {
        Solicitud solicitud = solicitudRepository.findById(idSolicitud)
                .orElseThrow(() -> new IllegalArgumentException("Solicitud no encontrada"));

        if (solicitud.getEstadoSolicitud() != EstadoSolicitud.ATENDIDA) {
            throw new IllegalStateException("Solo se puede cerrar una solicitud atendida");
        }

        solicitud.setEstadoSolicitud(EstadoSolicitud.CERRADA);

        HistorialSolicitud historial = HistorialSolicitud.builder()
                .fechaHora(LocalDateTime.now())
                .accionRealizada("Cierre de solicitud")
                .observaciones(observacionCierre)
                .solicitud(solicitud)
                .build();
        solicitud.getHistorial().add(historial);

        return SolicitudMapper.aResponseDTO(solicitudRepository.save(solicitud));
    }


}
