package com.histolyze.histolyze.service;

import com.histolyze.histolyze.dto.DsaSimpleDTO;
import com.histolyze.histolyze.dto.HLADonanteDTO;
import java.util.List;

public interface CrossmatchService {

    /**
     * Busca los resultados DSA más recientes de un paciente (receptor) por DNI o N° Muestra.
     * @param patientIdentifier DNI o Número de Muestra.
     * @return Lista de DSAResultadoDTO.
     */
    List<DsaSimpleDTO> getLatestDSAResults(String patientIdentifier);

    /**
     * Busca la tipificación HLA de un donante por su ID de paciente.
     * @param donanteId ID del paciente (donante).
     * @return HLADonanteDTO con un array simple de antígenos.
     */
    HLADonanteDTO getDonanteHLA(String donanteId);
}