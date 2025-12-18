package com.histolyze.histolyze.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.histolyze.histolyze.model.HlaDonante;

import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "trasplante")
public class Trasplante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_plante")
    private Long idTrasplante;

    @Column(nullable = false)
    private LocalDate fecha;

    @Embedded
    private HlaDonante hlaDonante;

    // (un trasplante pertenece a un antecedente)
    @ManyToOne
    @JoinColumn(name = "id_antecedente", nullable = false)
    @JsonBackReference
    private Antecedente antecedente;

    // Enum para tipo de trasplante
    public enum Tipo {
        DONANTECADAVERICO, DONANTEVIVO
    }

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Tipo tipo;
}
