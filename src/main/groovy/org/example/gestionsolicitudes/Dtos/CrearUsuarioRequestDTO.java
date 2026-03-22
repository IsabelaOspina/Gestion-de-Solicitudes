package org.example.gestionsolicitudes.Dtos;
import lombok.Data;
import org.example.gestionsolicitudes.Model.Rol;

@Data
public class CrearUsuarioRequestDTO {
    private String nombre;
    private String correo;
    private String password;
    private Rol rol;
}