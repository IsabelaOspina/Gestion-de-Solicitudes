package org.example.gestionsolicitudes.Mapper;

import org.example.gestionsolicitudes.Model.Usuario;
import org.example.gestionsolicitudes.Dtos.CrearUsuarioRequestDTO;
import org.example.gestionsolicitudes.Dtos.UsuarioResponseDTO;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class UsuarioMapper {


    public Usuario aEntidad(CrearUsuarioRequestDTO dto) {
        if (dto == null) return null;

        return Usuario.builder()
                .nombreUsuario(dto.getNombre())
                .correoElectronico(dto.getCorreo())
                .password(dto.getPassword())
                .rol(dto.getRol())
                .activo(true)
                .build();
    }

    public UsuarioResponseDTO aDTO(Usuario usuario) {
        if (usuario == null) return null;

        UsuarioResponseDTO dto = new UsuarioResponseDTO();
        dto.setIdUsuario(usuario.getIdUsuario());
        dto.setNombreUsuario(usuario.getNombreUsuario());
        dto.setCorreoElectronico(usuario.getCorreoElectronico());
        dto.setRol(usuario.getRol());
        dto.setActivo(usuario.getActivo());

        return dto;
    }

    public List<UsuarioResponseDTO> aDTOLista(List<Usuario> usuarios) {
        if (usuarios == null) return List.of();

        return usuarios.stream()
                .map(this::aDTO)
                .collect(Collectors.toList());
    }
}