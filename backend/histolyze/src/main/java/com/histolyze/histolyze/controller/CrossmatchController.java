package com.histolyze.histolyze.controller;

import com.histolyze.histolyze.dto.HLADonanteDTO;
import com.histolyze.histolyze.dto.DsaSimpleDTO;
import com.histolyze.histolyze.service.CrossmatchService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/crossmatch")
@CrossOrigin(origins = "http://127.0.0.1:5500") // Asegúrate que el puerto sea el correcto
public class CrossmatchController {

    private final CrossmatchService crossmatchService;

    public CrossmatchController(CrossmatchService crossmatchService) {
        this.crossmatchService = crossmatchService;
    }

    /**
     * Endpoint para obtener los resultados DSA (formato simple) de un paciente.
     */
    @GetMapping("/paciente/{identifier}/dsa")
    public ResponseEntity<List<DsaSimpleDTO>> getLatestDSAResults(@PathVariable String identifier) {
        try {
            // --- CAMBIO 3 ---
            // El tipo de la variable 'dsaResultados' también debe cambiar
            List<DsaSimpleDTO> dsaResultados = crossmatchService.getLatestDSAResults(identifier);
            if (dsaResultados.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(dsaResultados);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Endpoint para obtener el HLA (serológico) de un donante.
     */
    @GetMapping("/donante/{donanteId}/hla")
    public ResponseEntity<HLADonanteDTO> getDonanteHLA(@PathVariable String donanteId) {
        try {
            HLADonanteDTO hlaDonante = crossmatchService.getDonanteHLA(donanteId);
            return ResponseEntity.ok(hlaDonante);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}