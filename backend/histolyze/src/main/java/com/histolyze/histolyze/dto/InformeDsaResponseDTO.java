package com.histolyze.histolyze.dto;

import com.histolyze.histolyze.model.AnticuerpoAntiHLA;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

@Data
@AllArgsConstructor
public class InformeDsaResponseDTO {
    // Usamos el nuevo DTO para la cabecera
    private DsaEstudioDTO dsa;

    // Mantenemos la lista de entidades o DTOs para los anticuerpos
    private List<AnticuerpoAntiHLA> anticuerpos;
}