package com.histolyze.histolyze.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "dsa")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DSA {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_dsa")
    private Long idDsa;

    @Column(name = "numero_muestra", nullable = false, length = 100)
    private String numeroMuestra;

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(name = "nombre_paciente", nullable = false, length = 150)
    private String nombrePaciente;

    @Column(name = "medico_solicitante", length = 150)
    private String medicoSolicitante;

    @Column(name = "dni_paciente", length = 20)
    private String dniPaciente;

    @Column(columnDefinition = "TEXT")
    private String observaciones;

    // Relación con Paciente
    @ManyToOne
    @JoinColumn(name = "id_paciente", nullable = false)
    @JsonBackReference
    private Paciente paciente;

    // Relación con los resultados de anticuerpos
    @OneToMany(mappedBy = "dsa", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<AnticuerpoAntiHLA> anticuerpos;
}

