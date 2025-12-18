package com.histolyze.histolyze.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InformeFamiliarResponseDTO {
    private FamiliarReporteDTO paciente;
    private List<FamiliarReporteDTO> donantes;
    private String notaGeneral; // Para la nota global del informe
    private Long idHlaReferencia; // ID de TipificacionesHLA para guardar la nota
}