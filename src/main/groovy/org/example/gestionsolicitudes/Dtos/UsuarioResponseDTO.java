package org.example.gestionsolicitudes.Dtos;

import lombok.Data;
import org.example.gestionsolicitudes.Model.Rol;

@Data
public class UsuarioResponseDTO {
    private Long idUsuario;
    private String nombreUsuario;
    private String correoElectronico;
    private Rol rol;
    private boolean activo;
}
