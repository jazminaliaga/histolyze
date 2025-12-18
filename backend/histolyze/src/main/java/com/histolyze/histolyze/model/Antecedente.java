package com.histolyze.histolyze.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "antecedente")
public class Antecedente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idAntecedente;

    @Column(columnDefinition = "TEXT")
    private String diagnostico;

    @Column(columnDefinition = "TEXT")
    private String medicacion;

    private LocalDate fechaComienzoHemodialisis;

    private Boolean embarazos;
    private Integer cantidadEmbarazos;

    private Boolean tuvoTransfusiones;
    private LocalDate fechaTransfusiones;

    // Campo PD renombrado para claridad
    @Column(name = "proceso_donacion_transfusiones")
    private String procesoDonacion;

    private Boolean tuvoTrasplantesPrevios;

    @Enumerated(EnumType.STRING)
    private GrupoSanguineo grupoSanguineo;

    // --- RELACIONES CON PADRES ---
    @ManyToOne
    @JoinColumn(name = "id_paciente", nullable = false)
    @JsonBackReference
    private Paciente paciente;

    @ManyToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    // --- RELACIONES CON HIJOS (LISTAS) ---
    @OneToMany(mappedBy = "antecedente", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Transfusion> listaTransfusiones;

    @OneToMany(mappedBy = "antecedente", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Trasplante> listaTrasplantes;

    // --- Enum para Grupo Sanguíneo ---
    public enum GrupoSanguineo {
        A_POSITIVO, A_NEGATIVO,
        B_POSITIVO, B_NEGATIVO,
        AB_POSITIVO, AB_NEGATIVO,
        O_POSITIVO, O_NEGATIVO
    }
}