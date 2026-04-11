package org.example.gestionsolicitudes.Service;

import org.example.gestionsolicitudes.Model.Solicitud;
import org.springframework.stereotype.Service;

@Service
public class PromptService {
    public String construirPrompt(Solicitud solicitud) {

        StringBuilder prompt = new StringBuilder();

        prompt.append("Actúa como un asistente administrativo profesional.\n");
        prompt.append("Analiza la siguiente solicitud y genera un resumen breve, claro y formal.\n");
        prompt.append("El resumen debe tener máximo 3 líneas y no debe incluir títulos ni introducciones.\n\n");

        prompt.append("DATOS DE LA SOLICITUD:\n");
        prompt.append("Descripción: ").append(solicitud.getDescripcion()).append("\n");
        prompt.append("Estado: ").append(solicitud.getEstadoSolicitud()).append("\n");
        prompt.append("Tipo: ").append(solicitud.getTipoSolicitud()).append("\n");

        if (solicitud.getResponsableAsignado() != null) {
            prompt.append("Responsable: ")
                    .append(solicitud.getResponsableAsignado().getNombreUsuario())
                    .append("\n");
        }

        prompt.append("\nHISTORIAL RECIENTE:\n");

        if (solicitud.getHistorial() != null) {
            solicitud.getHistorial().stream().limit(5).forEach(h -> {
                prompt.append("- ")
                        .append(h.getFechaHora())
                        .append(": ")
                        .append(h.getAccionRealizada())
                        .append("\n");
            });
        }

        prompt.append("\nResponde SOLO con el resumen final.");

        return prompt.toString();
    }
}
