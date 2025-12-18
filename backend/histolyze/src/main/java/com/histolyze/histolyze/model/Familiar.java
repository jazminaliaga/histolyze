package com.histolyze.histolyze.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "familiar")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Familiar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idFamiliar;

    @Column(nullable = false)
    private LocalDate fechaRegistro;

    @Column(length = 150)
    private String numeroMuestra;

    @Column(nullable = false, length = 150)
    private String nombre;

    @Column(length = 20)
    private String dni;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Vinculo vinculo;

    @Enumerated(EnumType.STRING)
    private Antecedente.GrupoSanguineo grupoSanguineo;

    // Reutilizamos la clase Embeddable que ya tenés para los trasplantes
    @Embedded
    private HlaDonante hlaData;

    @Column(columnDefinition = "TEXT")
    private String observaciones;

    // Relación con Paciente
    @ManyToOne
    @JoinColumn(name = "id_paciente", nullable = false)
    @JsonBackReference
    private Paciente paciente;

    // Enum para Vínculo
    public enum Vinculo {
        PADRE,
        MADRE,
        HERMANO_A,
        HIJO_A,
        OTRO
    }
}