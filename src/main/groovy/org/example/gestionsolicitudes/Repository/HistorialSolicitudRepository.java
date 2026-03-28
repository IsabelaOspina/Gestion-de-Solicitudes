package org.example.gestionsolicitudes.Repository;

import org.example.gestionsolicitudes.Model.HistorialSolicitud;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HistorialSolicitudRepository extends JpaRepository<HistorialSolicitud, Long> {

    // Buscar historial por idSolicitud
    List<HistorialSolicitud> findBySolicitudIdSolicitud(Long idSolicitud);
}
