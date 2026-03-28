package org.example.gestionsolicitudes.Controller;

import org.example.gestionsolicitudes.Dtos.CrearUsuarioRequestDTO;
import org.example.gestionsolicitudes.Dtos.UsuarioResponseDTO;
import org.example.gestionsolicitudes.Model.Rol;
import org.example.gestionsolicitudes.Model.Usuario;
import org.example.gestionsolicitudes.Service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    // Crear usuario
    @PostMapping("/crear")
    public ResponseEntity<UsuarioResponseDTO> crearUsuario(@RequestBody CrearUsuarioRequestDTO dto) {
        UsuarioResponseDTO response = usuarioService.crearUsuario(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Obtener usuario por ID
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> obtenerPorId(@PathVariable Long id) {
        Usuario usuario = usuarioService.obtenerPorId(id);
        return ResponseEntity.ok(usuarioService.getUsuarioMapper().aResponseDTO(usuario));
    }

    // Obtener usuarios por rol
    @GetMapping("/rol/{rol}")
    public ResponseEntity<List<UsuarioResponseDTO>> obtenerUsuariosPorRol(@PathVariable Rol rol) {
        List<UsuarioResponseDTO> usuarios = usuarioService.obtenerUsuariosPorRol(rol);
        return ResponseEntity.ok(usuarios);
    }

    // Obtener usuario por correo
    @GetMapping("/correo/{correo}")
    public ResponseEntity<UsuarioResponseDTO> obtenerPorCorreo(@PathVariable String correo) {
        Usuario usuario = usuarioService.obtenerPorCorreo(correo);
        return ResponseEntity.ok(usuarioService.getUsuarioMapper().aResponseDTO(usuario));
    }

    // Validar usuario activo
    @GetMapping("/{id}/activo")
    public ResponseEntity<String> validarUsuarioActivo(@PathVariable Long id) {
        usuarioService.validarUsuarioActivo(id);
        return ResponseEntity.ok("Usuario activo");
    }

}
