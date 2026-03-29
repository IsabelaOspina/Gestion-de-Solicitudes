package org.example.gestionsolicitudes.Service;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.example.gestionsolicitudes.Dtos.CrearSolicitudRequestDTO;
import org.example.gestionsolicitudes.Dtos.PrioridadSolicitudRequestDTO;
import org.example.gestionsolicitudes.Dtos.SolicitudResponseDTO;
import org.example.gestionsolicitudes.Mapper.HistorialSolicitudesMapper;
import org.example.gestionsolicitudes.Mapper.SolicitudMapper;
import org.example.gestionsolicitudes.Model.*;
import org.example.gestionsolicitudes.Repository.HistorialSolicitudesRepository;
import org.example.gestionsolicitudes.Repository.SolicitudRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Getter
public class SolicitudService {

    private final SolicitudRepository solicitudRepository;

    private final UsuarioService usuarioService;

    private final HistorialSolicitudesRepository historialRepository;

    private final HistorialSolicitudesMapper historialMapper;

    private final SolicitudMapper solicitudMapper;

    // RF-01: Registrar solicitud
    @PreAuthorize("hasAnyRole('ESTUDIANTE','DOCENTE')")
    public SolicitudResponseDTO registrarSolicitud(CrearSolicitudRequestDTO dto, Long idSolicitante) {
        Usuario solicitante = usuarioService.obtenerUsuarioActivo(idSolicitante);

        if (solicitante.getRol() == Rol.ADMINISTRATIVO) {
            throw new IllegalStateException("Los administrativos no pueden ser solicitantes");
        }

        // Mapear DTO a entidad
        Solicitud solicitud = solicitudMapper.aEntidad(dto, solicitante);

        // Historial
        HistorialSolicitud historial = HistorialSolicitud.builder()
                .fechaHora(LocalDateTime.now())
                .accionRealizada("Registro de solicitud")
                .observaciones("Solicitud registrada por " + solicitante.getNombreUsuario())
                .solicitud(solicitud)
                .build();
        solicitud.getHistorial().add(historial);

        // Guardar y devolver DTO
        return solicitudMapper.aResponseDTO(solicitudRepository.save(solicitud));

    }

    // RF-03: Priorización
    @PreAuthorize("hasRole('ADMINISTRATIVO')")
    public SolicitudResponseDTO priorizarSolicitud(Long idSolicitud, PrioridadSolicitudRequestDTO dto) {
        Solicitud solicitud = solicitudRepository.findById(idSolicitud)
                .orElseThrow(() -> new IllegalArgumentException("Solicitud no encontrada"));

        solicitudMapper.actualizarPrioridad(solicitud, dto);
        solicitud.setEstadoSolicitud(EstadoSolicitud.CLASIFICADA);

        return solicitudMapper.aResponseDTO(solicitudRepository.save(solicitud));
    }

    // RF-05: Asignación de responsable
    @PreAuthorize("hasRole('ADMINISTRATIVO')")
    public SolicitudResponseDTO asignarResponsable(Long idSolicitud, Long idResponsable) {
        Solicitud solicitud = solicitudRepository.findById(idSolicitud)
                .orElseThrow(() -> new IllegalArgumentException("Solicitud no encontrada"));

        Usuario responsable = usuarioService.obtenerUsuarioActivo(idResponsable);

        if (!responsable.getActivo()) {
            throw new IllegalStateException("El responsable no está activo");
        }

        if (responsable.getRol() != Rol.ADMINISTRATIVO) {
            throw new IllegalStateException("Solo los administrativos pueden ser responsables");
        }

        solicitud.setResponsableAsignado(responsable);
        solicitud.setEstadoSolicitud(EstadoSolicitud.EN_ATENCION);

        // Registrar historial
        HistorialSolicitud historial = new HistorialSolicitud();
        historial.setFechaHora(LocalDateTime.now());
        historial.setAccionRealizada("Asignación de responsable");
        historial.setObservaciones("Asignado a " + responsable.getNombreUsuario());
        historial.setSolicitud(solicitud);

        solicitud.getHistorial().add(historial);

        // Guardar cambios
        solicitudRepository.save(solicitud);
        historialRepository.save(historial);

        return solicitudMapper.aResponseDTO(solicitud);

    }
    // RF-07: Consultas simples (estado, tipo, prioridad, responsable)
    @PreAuthorize("hasRole('ADMINISTRATIVO')")
    public List<SolicitudResponseDTO> consultarPorEstado(EstadoSolicitud estado) {
        return solicitudRepository.findByEstadoSolicitud(estado)
                .stream()
                .map(solicitudMapper::aResponseDTO)
                .toList();
    }

    @PreAuthorize("hasRole('ADMINISTRATIVO')")
    public List<SolicitudResponseDTO> consultarPorTipo(TipoSolicitud tipo) {
        return solicitudRepository.findByTipoSolicitud(tipo)
                .stream()
                .map(solicitudMapper::aResponseDTO)
                .toList();
    }

    @PreAuthorize("hasRole('ADMINISTRATIVO')")
    public List<SolicitudResponseDTO> consultarPorPrioridad(NivelPrioridad prioridad) {
        return solicitudRepository.findByNivelPrioridad(prioridad)
                .stream()
                .map(solicitudMapper::aResponseDTO)
                .toList();
    }

    @PreAuthorize("hasRole('ADMINISTRATIVO')")
    public List<SolicitudResponseDTO> consultarPorResponsable(Usuario responsable) {
        return solicitudRepository.findByResponsableAsignado(responsable)
                .stream()
                .map(solicitudMapper::aResponseDTO)
                .toList();
    }

    @PreAuthorize("hasRole('ADMINISTRATIVO')")
    public List<SolicitudResponseDTO> consultarPorRangoFechas(LocalDateTime desde, LocalDateTime hasta) {
        return solicitudRepository.findByFechaHoraRegistroBetween(desde, hasta)
                .stream()
                .map(solicitudMapper::aResponseDTO)
                .toList();
    }

    @PreAuthorize("hasRole('ADMINISTRATIVO')")
    public List<SolicitudResponseDTO> consultarPorEstadoYTipo(EstadoSolicitud estado, TipoSolicitud tipo) {
        return solicitudRepository.findByEstadoSolicitudAndTipoSolicitud(estado, tipo)
                .stream()
                .map(solicitudMapper::aResponseDTO)
                .toList();
    }

    @PreAuthorize("hasRole('ADMINISTRATIVO')")
    public List<SolicitudResponseDTO> consultarPorSolicitante(Usuario solicitante) {
        return solicitudRepository.findBySolicitante(solicitante)
                .stream()
                .map(solicitudMapper::aResponseDTO)
                .toList();
    }


    // RF-08: Cierre de solicitud
    @PreAuthorize("hasRole('ADMINISTRATIVO')")
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

        return solicitudMapper.aResponseDTO(solicitudRepository.save(solicitud));
    }


}
