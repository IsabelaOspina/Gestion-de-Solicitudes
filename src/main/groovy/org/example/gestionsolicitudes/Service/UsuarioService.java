package org.example.gestionsolicitudes.Service;

import org.example.gestionsolicitudes.Dtos.CrearUsuarioRequestDTO;
import org.example.gestionsolicitudes.Dtos.UsuarioResponseDTO;
import org.example.gestionsolicitudes.Mapper.UsuarioMapper;
import org.example.gestionsolicitudes.Model.Usuario;
import org.example.gestionsolicitudes.Repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private UsuarioMapper usuarioMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // 🔥 CREAR USUARIO
    public UsuarioResponseDTO crearUsuario(CrearUsuarioRequestDTO dto) {

        // 1. Validar si ya existe el correo
        if (usuarioRepository.existeCorreoElectronico(dto.getCorreo())) {
            throw new RuntimeException("El correo ya está registrado");
        }

        // 2. Convertir DTO → Entity
        Usuario usuario = usuarioMapper.aEntidad(dto);

        // 3. 🔐 Encriptar contraseña
        usuario.setPassword(passwordEncoder.encode(dto.getPassword()));

        // 4. Guardar en BD
        Usuario usuarioGuardado = usuarioRepository.save(usuario);

        // 5. Convertir a DTO de respuesta
        return usuarioMapper.aDTO(usuarioGuardado);
    }
}