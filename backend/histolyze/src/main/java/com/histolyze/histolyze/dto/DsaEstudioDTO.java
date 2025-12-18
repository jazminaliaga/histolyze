package com.histolyze.histolyze.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DsaEstudioDTO {
    private String nombrePaciente;
    private String dniPaciente;
    private String medicoSolicitante;
    private String numeroMuestra;
    private LocalDate fecha;
    private String observaciones;
}