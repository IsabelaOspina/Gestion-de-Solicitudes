package org.example.gestionsolicitudes.Model;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

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
    private Long idSolicitud;

    @NotBlank(message = "La solicitud debe tener una descripción")
    @Size(max = 250, message = "Máximo 250 caracteres")
    @Column(name = "Descripcion", nullable = false,length = 250)
    private String descripcion;

    @Column(updatable = false)
    private LocalDateTime fechaHoraRegistro;

    @Enumerated(EnumType.STRING)
    @Column(name="Canal_origen", nullable = false)
    private CanalOrigen canalOrigen;

    @Enumerated(EnumType.STRING)
    @Column(name="Tipo_Solicitud", nullable = false)
    private TipoSolicitud tipoSolicitud;

    @Enumerated(EnumType.STRING)
    @Column(name="Estado_Solicitud", nullable = false)
    private EstadoSolicitud estadoSolicitud;

    @Column(name = "fecha_limite", nullable = false)
    private LocalDateTime fechaLimite;

    @PrePersist
    public void asignarFechaRegistroYLimite() {
        this.fechaHoraRegistro = LocalDateTime.now();

        if (this.nivelPrioridad != null) {
            switch (this.nivelPrioridad) {
                case ALTA:
                    this.fechaLimite = this.fechaHoraRegistro.plusDays(3);
                    break;
                case MEDIA:
                    this.fechaLimite = this.fechaHoraRegistro.plusDays(7);
                    break;
                case BAJA:
                    this.fechaLimite = this.fechaHoraRegistro.plusDays(15);
                    break;
            }
        }
    }

    @ManyToOne
    @JoinColumn(name = "solicitante_id", nullable = false)
    private Usuario solicitante;

    @ManyToOne
    @JoinColumn(name = "responsable_id", nullable = false)
    private Usuario responsableAsignado;

    @Enumerated(EnumType.STRING)
    @Column(name = "Nivel_Prioridad", nullable = false)
    private NivelPrioridad nivelPrioridad;

    @NotBlank(message = "Se debe justificar la prioridad")
    @Size(max = 250, message = "Máximo 250 caracteres")
    @Column(name = "Justificacion_prioridad", nullable = false, unique = true,length = 250)
    private String justificacionPrioridad;

    @OneToMany(mappedBy = "solicitud", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HistorialSolicitud> historial;
}

