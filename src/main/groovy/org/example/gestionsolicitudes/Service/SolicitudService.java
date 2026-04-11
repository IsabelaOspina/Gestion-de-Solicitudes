package org.example.gestionsolicitudes.Service;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.example.gestionsolicitudes.Dtos.CrearSolicitudRequestDTO;
import org.example.gestionsolicitudes.Dtos.PrioridadSolicitudRequestDTO;
import org.example.gestionsolicitudes.Dtos.ResumenSolicitudResponseDTO;
import org.example.gestionsolicitudes.Dtos.SolicitudResponseDTO;
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
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (solicitante.getRol() == Rol.ADMINISTRATIVO) {
            throw new IllegalStateException("Los administrativos no pueden ser solicitantes");
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
                .orElseThrow(() -> new IllegalArgumentException("Solicitud no encontrada"));

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
            }
            else if (respuestaIA.contains("MEDIA")) {
                solicitud.setNivelPrioridad(NivelPrioridad.MEDIA);
            }
            else {
                solicitud.setNivelPrioridad(NivelPrioridad.BAJA);
            }

            if (respuestaIA.contains("JUSTIFICACION:")) {

                String justificacion = respuestaIA.split("JUSTIFICACION:")[1].trim();

                solicitud.setJustificacionPrioridad(justificacion);
            }

        } else {

            solicitudMapper.actualizarPrioridad(solicitud, dto);
        }

        solicitud.asignarFechaRegistroYLimite();

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

        if (solicitud.getEstadoSolicitud() == EstadoSolicitud.CERRADA) {
            throw new IllegalStateException("No se puede asignar responsable a una solicitud cerrada");
        }

        solicitud.setResponsableAsignado(responsable);
        solicitud.setEstadoSolicitud(EstadoSolicitud.EN_ATENCION);

        HistorialSolicitud historial = new HistorialSolicitud();
        historial.setFechaHora(LocalDateTime.now());
        historial.setAccionRealizada("Asignación de responsable");
        historial.setObservaciones("Asignado a " + responsable.getNombreUsuario());
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

    public ResumenSolicitudResponseDTO generarResumenSolicitud(Long idSolicitud) {

        Solicitud solicitud = solicitudRepository.findById(idSolicitud)
                .orElseThrow(() -> new IllegalArgumentException("Solicitud no encontrada"));

        String resumen = iaService.generarResumen(solicitud);

        ResumenSolicitudResponseDTO response = new ResumenSolicitudResponseDTO();
        response.setIdSolicitud(solicitud.getIdSolicitud());
        response.setEstadoSolicitud(solicitud.getEstadoSolicitud());
        response.setResumenGenerado(resumen);

        return response;
    }


}
