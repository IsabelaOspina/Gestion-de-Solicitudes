package org.example.gestionsolicitudes.Service;

import org.example.gestionsolicitudes.Model.CanalOrigen;
import org.example.gestionsolicitudes.Model.Solicitud;
import org.example.gestionsolicitudes.Model.TipoSolicitud;

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


    public String sugerirPrioridadFallback(String descripcion) {

        System.out.println("Fallback activado para prioridad (sin IA)");

        String texto = descripcion.toLowerCase().trim();

        String prioridad="";
        String impacto="";
        String justificacion = "";

        // PRIORIDAD ALTA
        if (texto.contains("caido") ||
                texto.contains("no funciona") ||
                texto.contains("error crítico") ||
                texto.contains("urgente") ||
                texto.contains("bloqueado") ||
                texto.contains("no responde")) {

            prioridad = "ALTA";
            impacto = "El sistema o funcionalidad está interrumpido completamente";
            justificacion = "Se detectan palabras clave asociadas a fallos críticos o interrupciones del servicio";
        }

        // PRIORIDAD MEDIA
        else if (texto.contains("lento") ||
                texto.contains("intermitente") ||
                texto.contains("error") ||
                texto.contains("problema")) {

            prioridad = "MEDIA";
            impacto = "El sistema presenta fallas parciales o degradación";
            justificacion = "Se identifican problemas que afectan el funcionamiento pero no lo detienen completamente";
        }

        // PRIORIDAD BAJA
        else if (texto.contains("consulta") ||
                texto.contains("información") ||
                texto.contains("mejora") ||
                texto.contains("sugerencia")) {

            prioridad = "BAJA";
            impacto = "No afecta directamente la operación del sistema";
            justificacion = "La solicitud corresponde a consultas o mejoras sin impacto crítico";
        }

        else {
            prioridad = "MEDIA";
            impacto = "Impacto no determinado claramente";
            justificacion = "No se detectaron palabras clave específicas";
        }

        return "PRIORIDAD: " + prioridad + "\n" +
                "IMPACTO: " + impacto + "\n" +
                "JUSTIFICACION: " + justificacion;
    }


}
