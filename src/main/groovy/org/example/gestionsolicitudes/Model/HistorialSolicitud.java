package org.example.gestionsolicitudes.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;


@Entity
@Table(name = "historial_solicitudes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HistorialSolicitud {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idHistorial;

    @NotNull
    @Column(name="fechaHora_accion", nullable = false)
    private LocalDateTime fechaHora;

    @NotBlank
    @Column(name="accion_realizada", nullable = false, length = 100)
    private String accionRealizada;

    @NotBlank
    @Column(name="observaciones", nullable = false, length = 255)
    private String observaciones;

    @ManyToOne
    @JoinColumn(name = "id_solicitud", nullable = false)
    private Solicitud solicitud;


}
