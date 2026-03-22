package org.example.gestionsolicitudes.Dtos;
import lombok.Data;
@Data
public class RegistroHistorialRequestDTO {
    private String accionRealizada;
    private String observaciones;
    private Long usuarioId;
}
