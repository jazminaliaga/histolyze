package com.histolyze.histolyze.controller;

import com.histolyze.histolyze.dto.InformeDsaResponseDTO;
import com.histolyze.histolyze.dto.InformeHlaResponseDTO;
import com.histolyze.histolyze.dto.InformeFamiliarResponseDTO;
import com.histolyze.histolyze.service.InformeService;
import com.histolyze.histolyze.service.PdfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/api/informes") // La URL base será http://localhost:8080/api/informes
public class InformeController {

    @Autowired
    private InformeService informeService;
    @Autowired
    private PdfService pdfService; // Inyectamos el servicio nuevo

    // Este endpoint coincide con la búsqueda del frontend para DSA
    @GetMapping("/dsa/buscar")
    public ResponseEntity<?> getInformeDsa(@RequestParam String dni) {
        try {
            InformeDsaResponseDTO datos = informeService.getDatosInformeDsa(dni);
            return ResponseEntity.ok(datos);
        } catch (RuntimeException e) {
            // Si el service lanza una excepción (ej: "Paciente no encontrado")
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    @PutMapping("/dsa/{idDsa}/observaciones")
    public ResponseEntity<?> guardarObservacionesDsa(
            @PathVariable Long idDsa,
            @RequestBody Map<String, String> body) {

        try {
            String observaciones = body.get("observaciones");
            if (observaciones == null) {
                return ResponseEntity.badRequest().body("El cuerpo debe contener 'observaciones'");
            }
            informeService.guardarObservacionDsa(idDsa, observaciones);
            return ResponseEntity.noContent().build();

        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/hla/buscar")
    public ResponseEntity<?> getInformeHla(@RequestParam String dni) {
        try {
            // El mock data  tiene la data anidada en 'paciente',
            // pero nuestro DTO ya es el objeto 'paciente'.
            // Para que el front funcione, debemos devolverlo anidado.
            InformeHlaResponseDTO datos = informeService.getDatosInformeHla(dni);
            return ResponseEntity.ok(Map.of("paciente", datos)); // Lo anidamos en un mapa
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    @PutMapping("/hla/{idHla}/observaciones")
    public ResponseEntity<?> guardarObservacionesHla(
            @PathVariable Long idHla,
            @RequestBody Map<String, String> body) {

        try {
            String observaciones = body.get("observaciones");
            if (observaciones == null) {
                return ResponseEntity.badRequest().body("El cuerpo debe contener 'observaciones'");
            }
            // Llamamos al nuevo método del servicio
            informeService.guardarObservacionHla(idHla, observaciones);
            return ResponseEntity.noContent().build(); // 204 No Content

        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/familiar/buscar")
    public ResponseEntity<?> getInformeFamiliar(@RequestParam String dni) {
        try {
            InformeFamiliarResponseDTO datos = informeService.getDatosInformeFamiliar(dni);
            // El frontend espera esta estructura directamente
            return ResponseEntity.ok(datos);
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    @PutMapping("/familiar/{idHlaReferencia}/observaciones")
    public ResponseEntity<?> guardarObservacionesFamiliar(
            @PathVariable Long idHlaReferencia,
            @RequestBody Map<String, String> body) {

        try {
            String observaciones = body.get("observaciones"); // Usamos la misma clave "observaciones"
            if (observaciones == null) {
                return ResponseEntity.badRequest().body("El cuerpo debe contener 'observaciones'");
            }
            informeService.guardarObservacionFamiliar(idHlaReferencia, observaciones);
            return ResponseEntity.noContent().build(); // 204 No Content

        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/descargar-pdf/dsa/{dni}")
    public ResponseEntity<byte[]> descargarInformeDsa(@PathVariable String dni) {

        // 1. Obtener datos
        InformeDsaResponseDTO datos = informeService.getDatosInformeDsa(dni);

        // 2. Preparamos el mapa
        Map<String, Object> variables = new HashMap<>();

        variables.put("dsa", datos.getDsa());
        variables.put("anticuerpos", datos.getAnticuerpos());

        variables.put("observaciones", datos.getDsa() != null ? datos.getDsa().getObservaciones() : "");

        // 3. Generar PDF
        byte[] pdfBytes = pdfService.generarPdf("reporte_dsa", variables);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=informe_dsa_" + dni + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }
}