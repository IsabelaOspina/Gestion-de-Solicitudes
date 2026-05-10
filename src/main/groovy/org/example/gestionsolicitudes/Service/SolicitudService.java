package org.example.gestionsolicitudes.Service;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.example.gestionsolicitudes.Dtos.*;
import org.example.gestionsolicitudes.Mapper.HistorialSolicitudesMapper;
import org.example.gestionsolicitudes.Mapper.SolicitudMapper;
import org.example.gestionsolicitudes.Model.*;
import org.example.gestionsolicitudes.Repository.HistorialSolicitudesRepository;
import org.example.gestionsolicitudes.Repository.SolicitudRepository;
import org.example.gestionsolicitudes.Repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.example.gestionsolicitudes.Exception.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Getter
public class SolicitudService {
    @Autowired
    private SolicitudRepository solicitudRepository;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private HistorialSolicitudesRepository historialRepository;

    @Autowired
    private HistorialSolicitudesMapper historialMapper;

    @Autowired
    private SolicitudMapper solicitudMapper;

    @Autowired
    private IAService iaService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    // RF-01: Registrar solicitud
    @PreAuthorize("hasAnyRole('ESTUDIANTE','DOCENTE')")
    public SolicitudResponseDTO registrarSolicitud(CrearSolicitudRequestDTO dto) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String correo = auth.getName();

        Usuario solicitante = usuarioRepository.findByCorreoElectronico(correo)
                .orElseThrow(() ->
                        new UsuarioNoEncontradoException());

        if (solicitante.getRol() == Rol.ADMINISTRATIVO) {
            throw new UsuarioNoAutorizadoException();
        }

        Solicitud solicitud = solicitudMapper.aEntidad(dto, solicitante);

        HistorialSolicitud historial = HistorialSolicitud.builder()
                .fechaHora(LocalDateTime.now())
                .accionRealizada("Registro de solicitud")
                .observaciones("Solicitud registrada por " + solicitante.getNombreUsuario())
                .solicitud(solicitud)
                .build();

        solicitud.getHistorial().add(historial);

        return solicitudMapper.aResponseDTO(solicitudRepository.save(solicitud));
    }

    // RF-03: Priorización
    @PreAuthorize("hasRole('ADMINISTRATIVO')")
    public SolicitudResponseDTO priorizarSolicitud(Long idSolicitud, PrioridadSolicitudRequestDTO dto) {

        Solicitud solicitud = solicitudRepository.findById(idSolicitud)
                .orElseThrow(SolicitudNoEncontradaException::new);;

        if (solicitud.getEstadoSolicitud() != EstadoSolicitud.REGISTRADA) {
            throw new SolicitudYaPriorizadaException();
        }

        String detalleAccion = "";

        if (Boolean.TRUE.equals(dto.getUsarIA())) {

            String respuestaIA = iaService.sugerirPrioridad(
                    solicitud.getDescripcion(),
                    solicitud.getTipoSolicitud(),
                    solicitud.getCanalOrigen()
            );

            System.out.println("RESPUESTA COMPLETA DE LA IA:");
            System.out.println(respuestaIA);

            if (respuestaIA.contains("ALTA")) {
                solicitud.setNivelPrioridad(NivelPrioridad.ALTA);
            } else if (respuestaIA.contains("MEDIA")) {
                solicitud.setNivelPrioridad(NivelPrioridad.MEDIA);
            } else {
                solicitud.setNivelPrioridad(NivelPrioridad.BAJA);
            }

            if (respuestaIA.contains("JUSTIFICACION:")) {
                String justificacion = respuestaIA.split("JUSTIFICACION:")[1].trim();
                solicitud.setJustificacionPrioridad(justificacion);
            }

            detalleAccion = "Priorización automática (IA). Prioridad: "
                    + solicitud.getNivelPrioridad()
                    + ". Justificación: "
                    + (solicitud.getJustificacionPrioridad() != null ? solicitud.getJustificacionPrioridad() : "No especificada");

        } else {

            solicitudMapper.actualizarPrioridad(solicitud, dto);

            String impacto = (dto.getImpacto() != null && !dto.getImpacto().isBlank())
                    ? ". Impacto: " + dto.getImpacto()
                    : "";

            detalleAccion = "Priorización manual. Prioridad: "
                    + solicitud.getNivelPrioridad()
                    + impacto;
        }

        solicitud.asignarFechaRegistroYLimite();
        solicitud.setEstadoSolicitud(EstadoSolicitud.CLASIFICADA);

        // HISTORIAL
        HistorialSolicitud historial = new HistorialSolicitud();
        historial.setFechaHora(LocalDateTime.now());
        historial.setAccionRealizada("Priorización de solicitud");
        historial.setObservaciones(detalleAccion);
        historial.setSolicitud(solicitud);

        if (solicitud.getHistorial() == null) {
            solicitud.setHistorial(new ArrayList<>());
        }

        solicitud.getHistorial().add(historial);

        solicitudRepository.save(solicitud);

        return solicitudMapper.aResponseDTO(solicitud);
    }

    // RF-05: Asignación de responsable
    @PreAuthorize("hasRole('ADMINISTRATIVO')")
    public SolicitudResponseDTO asignarResponsable(Long idSolicitud, Long idResponsable) {

        Solicitud solicitud = solicitudRepository.findById(idSolicitud)
                .orElseThrow(SolicitudNoEncontradaException::new);

        if (solicitud.getNivelPrioridad() == null ||
                solicitud.getEstadoSolicitud() != EstadoSolicitud.CLASIFICADA) {

            throw new SolicitudNoPriorizadaException();
        }

        Usuario responsable = usuarioService.obtenerUsuarioActivo(idResponsable);

        if (!responsable.getActivo()) {
            throw new UsuarioInactivoException();
        }

        if (responsable.getRol() != Rol.ADMINISTRATIVO) {
            throw new UsuarioNoAutorizadoException();
        }

        if (solicitud.getEstadoSolicitud() == EstadoSolicitud.CERRADA) {
            throw new SolicitudCerradaException();
        }

        solicitud.setResponsableAsignado(responsable);

        solicitud.setEstadoSolicitud(EstadoSolicitud.EN_ATENCION);

        HistorialSolicitud historial = new HistorialSolicitud();
        historial.setFechaHora(LocalDateTime.now());
        historial.setAccionRealizada("Asignación de responsable");
        historial.setObservaciones(
                "Asignado a " + responsable.getNombreUsuario());
        historial.setSolicitud(solicitud);

        if (solicitud.getHistorial() == null) {
            solicitud.setHistorial(new ArrayList<>());
        }

        solicitud.getHistorial().add(historial);

        solicitudRepository.save(solicitud);

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
    public List<SolicitudResponseDTO> consultarPorResponsable(Long idResponsable) {
        Usuario responsable = usuarioService.obtenerUsuarioActivo(idResponsable);
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
    public List<SolicitudResponseDTO> consultarPorSolicitante(Long idSolicitante) {
        Usuario solicitante = usuarioService.obtenerUsuarioActivo(idSolicitante);
        return solicitudRepository.findBySolicitante(solicitante)
                .stream()
                .map(solicitudMapper::aResponseDTO)
                .toList();
    }


    // RF-08: Cierre de solicitud
    @PreAuthorize("hasRole('ADMINISTRATIVO')")
    public SolicitudResponseDTO cerrarSolicitud(Long idSolicitud, String observacionCierre) {
        Solicitud solicitud = solicitudRepository.findById(idSolicitud)
                .orElseThrow(SolicitudNoEncontradaException::new);

        if (solicitud.getEstadoSolicitud() != EstadoSolicitud.ATENDIDA) {
            throw new SolicitudNoAtendidaException();
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

    public ResumenSolicitudResponseDTO generarResumenSolicitud(Long idSolicitud) {

        Solicitud solicitud = solicitudRepository.findById(idSolicitud)
                .orElseThrow(SolicitudNoEncontradaException::new);

        String resumen = iaService.generarResumen(solicitud);

        ResumenSolicitudResponseDTO response = new ResumenSolicitudResponseDTO();
        response.setIdSolicitud(solicitud.getIdSolicitud());
        response.setEstadoSolicitud(solicitud.getEstadoSolicitud());
        response.setResumenGenerado(resumen);

        return response;
    }
    @PreAuthorize("hasRole('ADMINISTRATIVO')")
    public SolicitudResponseDTO atenderSolicitud(Long idSolicitud, String observacion) {

        Solicitud solicitud = solicitudRepository.findById(idSolicitud)
                .orElseThrow(SolicitudNoEncontradaException::new);
        if (solicitud.getEstadoSolicitud() != EstadoSolicitud.EN_ATENCION) {
            throw new SolicitudNoEnAtencionException();
        }

        if (observacion == null || observacion.trim().isEmpty()) {
            throw new SolicitudException(
                    "Debe describir cómo se atendió la solicitud");
        }

        // Verificar que tenga responsable asignado
        if (solicitud.getResponsableAsignado() == null) {
            throw new SolicitudSinResponsableException();
        }

        // Obtener usuario autenticado
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String correo = auth.getName();

        Usuario usuarioActual = usuarioRepository.findByCorreoElectronico(correo)
                .orElseThrow(UsuarioNoEncontradoException::new);

        // Verificar que sea el responsable asignado
        if (!solicitud.getResponsableAsignado().getIdUsuario()
                .equals(usuarioActual.getIdUsuario())) {
            throw new UsuarioNoAutorizadoException();
        }

        // Cambiar estado
        solicitud.setEstadoSolicitud(EstadoSolicitud.ATENDIDA);

        String mensaje = "Solicitud atendida por " + usuarioActual.getNombreUsuario() +
                ". Acción realizada: " + observacion;

        // Guardar historial
        HistorialSolicitud historial = HistorialSolicitud.builder()
                .fechaHora(LocalDateTime.now())
                .accionRealizada("Atención de solicitud")
                .observaciones(mensaje)
                .solicitud(solicitud)
                .build();

        solicitud.getHistorial().add(historial);

        return solicitudMapper.aResponseDTO(solicitudRepository.save(solicitud));
    }
}
