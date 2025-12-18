package com.histolyze.histolyze.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "transfusion")
public class Transfusion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_transfusion")
    private Long idTransfusion;

    @Column(nullable = false)
    private LocalDate fecha;

    // Enum para tipo de transfusion
    public enum Tipo {
        CRIOPRECIPITADOS, PLAQUETOFERESIS, PLASMAFERESIS, SEDIMENTO_GLOBULAR
    }

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Transfusion.Tipo tipo;

    // (una transfusion pertenece a un antecedente)
    @ManyToOne
    @JoinColumn(name = "id_antecedente", nullable = false)
    @JsonBackReference
    private Antecedente antecedente;

}