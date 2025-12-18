package com.histolyze.histolyze.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import java.util.List;

@Entity
@Table(name = "paciente")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = {"idPaciente", "dni"})
@ToString(exclude = {"antecedentes", "tipificacionesHLA", "dsa", "crossmatchContraPanel", "creadoPor", "completadoPor", "familiares"})
public class Paciente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPaciente;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false, length = 100)
    private String apellido;

    @Column(nullable = false, unique = true, length = 20)
    private String dni;

    private LocalDate fechaNacimiento;

    @Column(length = 255)
    private String domicilio;

    @Column(length = 20)
    private String telefono;

    @Column(length = 50)
    private String numeroMuestra;

    @Column(length = 150)
    private String centroDialisis;

    @Column(length = 150)
    private String centroTx;

    @Column(length = 100)
    private String mutual;

    @Column(length = 100)
    private String medicoSolicitante;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "creado_por", nullable = false)
    private Usuario creadoPor;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "completado_por")
    private Usuario completadoPor;

    @OneToMany(mappedBy = "paciente", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<Antecedente> antecedentes;

    @OneToMany(mappedBy = "paciente", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<TipificacionesHLA> tipificacionesHLA;

    @OneToMany(mappedBy = "paciente", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<DSA> dsa;

    @OneToMany(mappedBy = "paciente", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<CrossmatchContraPanel> crossmatchContraPanel;

    @OneToMany(mappedBy = "paciente", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<Familiar> familiares;

}

