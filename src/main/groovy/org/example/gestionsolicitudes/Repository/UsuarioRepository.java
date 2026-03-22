package org.example.gestionsolicitudes.Repository;

import org.example.gestionsolicitudes.Model.Rol;
import org.example.gestionsolicitudes.Model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    boolean existeCorreoElectronico(String correoElectronico);

    Optional<Usuario> findByCorreoElectronico(String correoElectronico);

    // Buscar usuarios activos
    List<Usuario> findByActivoTrue();

    // Buscar por rol
    List<Usuario> findByRol(Rol rol);

}
