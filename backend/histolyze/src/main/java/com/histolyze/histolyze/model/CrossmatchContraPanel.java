package com.histolyze.histolyze.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "crossmatch_contra_panel")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CrossmatchContraPanel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_crossmatch")
    private Long idCrossmatch;

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(name = "numero_muestra", length = 150)
    private String numeroMuestra;

    @Column(name = "anti_hla_1")
    private Integer antiHla1;

    @Column(name = "anti_hla_2")
    private Integer antiHla2;

    // Enum para ANTIMICA
    public enum Resultado {
        POSITIVO, NEGATIVO
    }

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Resultado resultado;

    @Column(name = "anticuerpos_no_confirmados", length = 255)
    private String anticuerposNoConfirmados;

    // Relación con Paciente
    @ManyToOne
    @JoinColumn(name = "id_paciente", nullable = false)
    @JsonBackReference
    private Paciente paciente;
}
