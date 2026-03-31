package org.example.gestionsolicitudes.Repository;

import org.example.gestionsolicitudes.Model.Rol;
import org.example.gestionsolicitudes.Model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    boolean existsByCorreoElectronico(String correoElectronico);
    // buscar usuario por correo
    Optional<Usuario> findByCorreoElectronico(String correoElectronico);

    // Buscar por rol
    List<Usuario> findByRol(Rol rol);

}
