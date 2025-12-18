package com.histolyze.histolyze.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class InformeHlaResponseDTO {

    private Long idHla;

    // Datos del paciente
    private String nombre;
    private String dni;
    private String muestra;
    private String grupoSanguineo;

    // El objeto HLA anidado
    private HlaSimpleDTO hla;
}