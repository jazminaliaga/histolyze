package com.histolyze.histolyze.controller;

import com.histolyze.histolyze.model.DSA;
import com.histolyze.histolyze.service.ReportParsingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/reports")
public class ReportUploadController {

    @Autowired
    private ReportParsingService reportParsingService;

    /**
     * Endpoint para subir, parsear y GUARDAR un reporte DSA.
     * Es transaccional.
     */
    @PostMapping("/upload-dsa") // Nueva ruta más clara
    public ResponseEntity<?> uploadAndParseDsa(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Por favor, selecciona un archivo.");
        }

        try {
            // 1. Llamamos al servicio para que haga TODO el trabajo
            DSA savedDsa = reportParsingService.parseAndSaveDsaReport(file);

            // 2. Si todo sale bien, devolvemos un mensaje de éxito
            return ResponseEntity.ok("Reporte DSA guardado con éxito. ID: " + savedDsa.getIdDsa());

        } catch (Exception e) {
            e.printStackTrace();
            // Si algo falla (ej: paciente no encontrado), la transacción hace rollback
            return ResponseEntity.internalServerError().body("Error al guardar el reporte: " + e.getMessage());
        }
    }
}