package org.example.gestionsolicitudes.Service;

import org.example.gestionsolicitudes.Dtos.CrearSolicitudRequestDTO;
import org.example.gestionsolicitudes.Model.CanalOrigen;
import org.example.gestionsolicitudes.Model.TipoSolicitud;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class IAService {

    @Value("${gemini.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    //RF-09
    public String generarResumen(String prompt) {

        String url = "https://generativelanguage.googleapis.com/v1/models/gemini-2.5-flash:generateContent?key=" + apiKey;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = Map.of(
                "contents", List.of(
                        Map.of(
                                "parts", List.of(
                                        Map.of("text", prompt)
                                )
                        )
                )
        );

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);

        List<Map<String, Object>> candidates =
                (List<Map<String, Object>>) response.getBody().get("candidates");

        Map<String, Object> content =
                (Map<String, Object>) candidates.get(0).get("content");

        List<Map<String, Object>> parts =
                (List<Map<String, Object>>) content.get("parts");

        return parts.get(0).get("text").toString();
    }

    //RF-10
    public String sugerirPrioridad(String descripcion, TipoSolicitud tipoSolicitud, CanalOrigen canalOrigen) {

        String prompt = """
                Eres un sistema que clasifica solicitudes.
                
                Analiza la información y responde SOLO en este formato exacto:
                
                PRIORIDAD: BAJA o MEDIA o ALTA
                IMPACTO: (una frase corta sobre el impacto de la solicitud)
                JUSTIFICACION: (explicación breve de por qué se asigna esa prioridad)
                
                No agregues texto adicional.
                
                Información de la solicitud:
                
                Tipo de solicitud: """ + tipoSolicitud + """
                Canal de origen: """ + canalOrigen + """
                Descripción: """ + descripcion + "";


        String url = "https://generativelanguage.googleapis.com/v1/models/gemini-2.5-flash:generateContent?key=" + apiKey;


        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);


        Map<String, Object> body = Map.of(
                "contents", List.of(
                        Map.of(
                                "parts", List.of(
                                        Map.of("text", prompt)
                                )
                        )
                )
        );


        HttpEntity<Map<String, Object>> requestHttp = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(url, requestHttp, Map.class);


        List<Map<String, Object>> candidates =
                (List<Map<String, Object>>) response.getBody().get("candidates");

        Map<String, Object> content =
                (Map<String, Object>) candidates.get(0).get("content");

        List<Map<String, Object>> parts =
                (List<Map<String, Object>>) content.get("parts");

        return parts.get(0).get("text").toString();
    }
}