package org.example.gestionsolicitudes.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "usuarios")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idUsuario;

    @NotBlank(message = "El nombre del usuario es obligatorio")
    @Column(name="Nombre", nullable = false)
    private String nombreUsuario;

    @NotBlank(message = "El correo electrónico es obligatorio")
    @Column(name="Correo_electronico",nullable = false, unique = true)
    private String correoElectronico;

    @NotNull(message = "El rol del usuario es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(name="Rol",nullable = false)
    private Rol rol;

    @NotNull(message = "El estado del usuario es obligatorio")
    @Column(name="Activo",nullable = false)
    private Boolean activo;

    @OneToMany(mappedBy = "solicitante")
    private List<Solicitud> solicitudesCreadas;

    @OneToMany(mappedBy = "responsableAsignado")
    private List<Solicitud> solicitudesAsignadas;

    @PrePersist
    public void prePersist() {
        if (activo == null) {
            activo = true; // Por defecto, el usuario estará activo al ser creado
        }
    }


}
