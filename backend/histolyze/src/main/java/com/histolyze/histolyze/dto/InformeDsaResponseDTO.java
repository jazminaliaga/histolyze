package com.histolyze.histolyze.dto;

import com.histolyze.histolyze.model.AnticuerpoAntiHLA;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

@Data
@AllArgsConstructor
public class InformeDsaResponseDTO {
    // Corresponde a la clave "dsa" del mock
    private DsaSimpleDTO dsa;

    // Corresponde a la clave "anticuerpos" del mock
    private List<AnticuerpoAntiHLA> anticuerpos;
}