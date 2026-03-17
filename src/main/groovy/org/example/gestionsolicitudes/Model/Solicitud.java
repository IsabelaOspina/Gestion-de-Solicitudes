package org.example.gestionsolicitudes.Model;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "solicitudes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Solicitud {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "La solicitud debe tener una descripción")
    @Size(max = 250, message = "Máximo 250 caracteres")
    @Column(name = "Descripcion", nullable = false, unique = true,length = 250)
    private String descripcion;

    @Column(updatable = false)
    private LocalDateTime fechaHoraRegistro;

    @Enumerated(EnumType.STRING)
    @Column(name="Canal_origen", nullable = false)
    private CanalOrigen canalOrigen;

    @Enumerated(EnumType.STRING)
    @Column(name="Tipo_Solicitud", nullable = false)
    private TipoSolicitud TipoSolicitud;

    @Enumerated(EnumType.STRING)
    @Column(name="Estado_Solicitud", nullable = false)
    private EstadoSolicitud EstadoSolicitud;

    @Column(name = "fecha_limite", nullable = false)
    private LocalDateTime fechaLimite;

    @PrePersist
    public void asignarFechaRegistroYLimite() {
        this.fechaHoraRegistro = LocalDateTime.now();
        this.fechaLimite = this.fechaHoraRegistro.plusDays(15);
    }

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario solicitante;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario ResponsableAsignado;

    @Enumerated(EnumType.STRING)
    @Column(name = "Nivel_Prioridad", nullable = false)
    private NivelPrioridad nivelPrioridad;

    @NotBlank(message = "Se debe justificar la prioridad")
    @Size(max = 250, message = "Máximo 250 caracteres")
    @Column(name = "Justificacion de prioridad", nullable = false, unique = true,length = 250)
    private String justificacionPrioridad;

    
}

