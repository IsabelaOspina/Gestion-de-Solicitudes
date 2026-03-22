package org.example.gestionsolicitudes.Dtos;
import lombok.Data;


@Data
public class CrearUsuarioRequestDTO {
    private String nombre;
    private String correo;
    private String contrasena;
    private Rol rol;
}