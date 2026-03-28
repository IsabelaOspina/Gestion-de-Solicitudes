package org.example.gestionsolicitudes.Repository;

import org.example.gestionsolicitudes.Model.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


public interface SolicitudRepository extends JpaRepository<Solicitud, Long> {

    // Consultar por ID
    Optional<Solicitud> findById(Long aLong);

    // Consultar por estado
    List<Solicitud> findByEstadoSolicitud(EstadoSolicitud estado);

    // Consultar por tipo de solicitud
    List<Solicitud> findByTipoSolicitud(TipoSolicitud tipo);

    // Consultar por nivel de prioridad
    List<Solicitud> findByNivelPrioridad(NivelPrioridad prioridad);

    // Consultar por responsable asignado
    List<Solicitud> findByResponsableAsignado(Usuario responsable);

    // Consultar por rango de fechas
    List<Solicitud> findByFechaHoraRegistroBetween(LocalDateTime desde, LocalDateTime hasta);

    // Consultar combinando estado y tipo
    List<Solicitud> findByEstadoSolicitudAndTipoSolicitud(EstadoSolicitud estado, TipoSolicitud tipo);

    // Consultar por solicitante
    List<Solicitud> findBySolicitante(Usuario solicitante);
}
