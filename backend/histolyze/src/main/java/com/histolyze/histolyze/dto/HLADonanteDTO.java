package com.histolyze.histolyze.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

// DTO para representar el HLA del donante en el formato que espera el frontend
// Contiene una lista simple de todos los antígenos para la lógica de comparación.
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HLADonanteDTO {
    private List<String> hla;
    private String nombreDonante;
}