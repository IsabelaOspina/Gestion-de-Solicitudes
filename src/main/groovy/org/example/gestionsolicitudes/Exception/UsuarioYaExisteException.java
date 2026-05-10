package org.example.gestionsolicitudes.Exception;


public class UsuarioYaExisteException extends RuntimeException {

    public UsuarioYaExisteException() {
        super("El correo ya está registrado");
    }
}