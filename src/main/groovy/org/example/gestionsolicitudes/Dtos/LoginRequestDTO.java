package org.example.gestionsolicitudes.Dtos;
import lombok.Data;

@Data
public class LoginRequestDTO {
    private String correoElectronico;
    private String password;
}