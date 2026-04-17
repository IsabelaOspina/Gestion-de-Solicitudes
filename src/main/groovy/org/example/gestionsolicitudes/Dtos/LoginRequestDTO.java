package org.example.gestionsolicitudes.Dtos;

import lombok.Data;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Data
public class LoginRequestDTO {

    @NotBlank(message = "El correo electrónico es obligatorio")
    @Email(message = "Debe proporcionar un correo electrónico válido")
    private String correoElectronico;

    @NotBlank(message = "La contraseña es obligatoria")
    private String password;
}