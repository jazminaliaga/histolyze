package com.histolyze.histolyze.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "tipificaciones_hla")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TipificacionesHLA {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_hla")
    private Long idHla;

    @Column(name = "numero_muestra", nullable = false, length = 150)
    private String numeroMuestra;

    @Column(name = "locusA_01", length = 50)
    private String locusA01;

    @Column(name = "locusA_02", length = 50)
    private String locusA02;

    @Column(name = "locusB_01", length = 50)
    private String locusB01;

    @Column(name = "locusB_02", length = 50)
    private String locusB02;

    @Column(name = "locusBw_01", length = 50)
    private String locusBw01;

    @Column(name = "locusBw_02", length = 50)
    private String locusBw02;

    @Column(name = "locusC_01", length = 50)
    private String locusC01;

    @Column(name = "locusC_02", length = 50)
    private String locusC02;

    @Column(name = "locusDR_01", length = 50)
    private String locusDR01;

    @Column(name = "locusDR_02", length = 50)
    private String locusDR02;

    @Column(name = "locusDR_Largo_01", length = 50)
    private String locusDrLargo01;

    @Column(name = "locusDR_Largo_02", length = 50)
    private String locusDrLargo02;

    @Column(name = "locusDQA_01", length = 50)
    private String locusDQA01;

    @Column(name = "locusDQA_02", length = 50)
    private String locusDQA02;

    @Column(name = "locusDQB_01", length = 50)
    private String locusDQB01;

    @Column(name = "locusDQB_02", length = 50)
    private String locusDQB02;

    @Column(name = "locusDPA_01", length = 50)
    private String locusDPA01;

    @Column(name = "locusDPA_02", length = 50)
    private String locusDPA02;

    @Column(name = "locusDPB_01", length = 50)
    private String locusDPB01;

    @Column(name = "locusDPB_02", length = 50)
    private String locusDPB02;

    @Column(name = "fecha_registro")
    private LocalDate fechaRegistro;

    @Column(name = "grupo_sanguineo", length = 10)
    private String grupoSanguineo;

    @Column(columnDefinition = "TEXT")
    private String observaciones;

    @Column(name = "observaciones_familiar", columnDefinition = "TEXT")
    private String observacionesFamiliar;

    @ManyToOne
    @JoinColumn(name = "id_paciente", nullable = false)
    @JsonBackReference
    private Paciente paciente;
}
