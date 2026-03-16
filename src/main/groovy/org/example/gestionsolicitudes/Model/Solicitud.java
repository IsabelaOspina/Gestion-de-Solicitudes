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
    @Column(name="Canal origen", nullable = false)
    private CanalOrigen canalOrigen;

    @NotBlank(message = "Se debe justificar la prioridad")
    @Size(max = 250, message = "Máximo 250 caracteres")
    @Column(name = "Justificacion de prioridad", nullable = false, unique = true,length = 250)
    private String justificacionPrioridad;






}
