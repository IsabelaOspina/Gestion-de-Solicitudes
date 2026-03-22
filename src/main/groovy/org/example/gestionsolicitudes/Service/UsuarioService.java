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

    public UsuarioResponseDTO crearUsuario(CrearUsuarioRequestDTO dto) {

        if (usuarioRepository.existeCorreoElectronico(dto.getCorreo())) {
            throw new RuntimeException("El correo ya está registrado");
        }

        Usuario usuario = usuarioMapper.aEntidad(dto);
        usuario.setPassword(passwordEncoder.encode(dto.getPassword()));

        Usuario usuarioGuardado = usuarioRepository.save(usuario);

        return usuarioMapper.aDTO(usuarioGuardado);
    }

    public Usuario obtenerPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    public Usuario obtenerPorCorreo(String correo) {
        return usuarioRepository.findByCorreoElectronico(correo)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    public void validarUsuarioActivo(Long id) {
        Usuario usuario = obtenerPorId(id);

        if (!usuario.getActivo()) {
            throw new RuntimeException("El usuario está inactivo");
        }
    }

    public Usuario obtenerUsuarioActivo(Long id) {
        Usuario usuario = obtenerPorId(id);

        if (!usuario.getActivo()) {
            throw new RuntimeException("El usuario está inactivo");
        }

        return usuario;
    }
}