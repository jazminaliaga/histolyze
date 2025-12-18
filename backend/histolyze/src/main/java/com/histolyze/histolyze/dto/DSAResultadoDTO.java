package com.histolyze.histolyze.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// DTO para representar un resultado simple de anticuerpo DSA para el Crossmatch
// Contiene la información mínima necesaria para el match y el resaltado visual en el frontend.
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DSAResultadoDTO {
    private String antigen;
    private String titre; // Título (ej: Bajo, Medio, Alto)
    private String c1q;   // Resultado C1q (ej: Positivo, Negativo)
}