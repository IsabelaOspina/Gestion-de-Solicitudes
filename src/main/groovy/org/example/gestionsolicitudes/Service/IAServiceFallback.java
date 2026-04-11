package org.example.gestionsolicitudes.Service;

import org.example.gestionsolicitudes.Model.Solicitud;
import org.example.gestionsolicitudes.Model.TipoSolicitud;
import org.springframework.stereotype.Service;

@Service
public class IAServiceFallback {

    public String generarResumenConDatos(Solicitud solicitud, Exception e) {

        StringBuilder resumen = new StringBuilder();

        resumen.append("Solicitud #").append(solicitud.getIdSolicitud()).append("\n");
        resumen.append("Estado: ").append(solicitud.getEstadoSolicitud()).append("\n");
        resumen.append("Descripción: ").append(solicitud.getDescripcion()).append("\n");
        resumen.append("Tipo: ").append(solicitud.getTipoSolicitud()).append("\n");

        if (solicitud.getResponsableAsignado() != null) {
            resumen.append("Responsable: ")
                    .append(solicitud.getResponsableAsignado().getNombreUsuario())
                    .append("\n");
        }

        if (solicitud.getNivelPrioridad() != null) {
            resumen.append("Prioridad: ").append(solicitud.getNivelPrioridad()).append("\n");
        }

        if (solicitud.getFechaHoraRegistro() != null) {
            resumen.append("Fecha: ").append(solicitud.getFechaHoraRegistro()).append("\n");
        }

        resumen.append("\nHistorial reciente:\n");

        if (solicitud.getHistorial() != null) {
            solicitud.getHistorial().stream().limit(5).forEach(h -> {
                resumen.append("- ")
                        .append(h.getFechaHora())
                        .append(": ")
                        .append(h.getAccionRealizada())
                        .append("\n");
            });
        }

        resumen.append("\n[Fallback: generado con datos locales]");

        return resumen.toString();
    }


    public String sugerirPrioridadFallback(String descripcion, TipoSolicitud tipoSolicitud) {

        System.out.println("Fallback activado para prioridad (sin IA)");

        String texto = descripcion.toLowerCase().trim();

        String prioridad="";
        String impacto="";
        String justificacion = "";

        switch (tipoSolicitud) {

            case CANCELACION_ASIGNATURA:
                prioridad = "ALTA";
                impacto = "Puede afectar directamente el semestre del estudiante";
                justificacion = "Cancelación de asignatura requiere gestión oportuna";
                break;

            case REGISTRO_ASIGNATURA:
                prioridad = "ALTA";
                impacto = "Impacta el proceso de matrícula";
                justificacion = "El registro de asignaturas es crítico en tiempos académicos";
                break;

            case SOLICITUD_CUPO:
                prioridad = "MEDIA";
                impacto = "Afecta el acceso a una asignatura";
                justificacion = "Depende de disponibilidad de cupos";
                break;

            case HOMOLOGACION:
                prioridad = "MEDIA";
                impacto = "Proceso administrativo académico";
                justificacion = "Requiere validación pero no es urgente";
                break;

            case CONSULTA_ACADEMICA:
                prioridad = "BAJA";
                impacto = "No afecta directamente procesos académicos";
                justificacion = "Es una solicitud informativa";
                break;

            default:
                prioridad = "MEDIA";
                impacto = "Impacto no determinado";
                justificacion = "Tipo de solicitud no identificado";
        }

        if (texto.contains("urgente") ||
                texto.contains("ya") ||
                texto.contains("inmediato")) {

            prioridad = "ALTA";
            justificacion += " | Ajustada por urgencia en la descripción";
        }

        return "PRIORIDAD: " + prioridad + "\n" +
                "IMPACTO: " + impacto + "\n" +
                "JUSTIFICACION: " + justificacion;
    }
}
