package com.histolyze.histolyze.controller;

import com.histolyze.histolyze.dto.FamiliarReporteDTO;
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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/api/informes")
public class InformeController {

    @Autowired
    private InformeService informeService;

    @Autowired
    private PdfService pdfService;

    // --- Endpoints existentes (JSON) ---

    @GetMapping("/dsa/buscar")
    public ResponseEntity<?> getInformeDsa(@RequestParam String dni) {
        try {
            InformeDsaResponseDTO datos = informeService.getDatosInformeDsa(dni);
            return ResponseEntity.ok(datos);
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    @PutMapping("/dsa/{idDsa}/observaciones")
    public ResponseEntity<?> guardarObservacionesDsa(@PathVariable Long idDsa, @RequestBody Map<String, String> body) {
        try {
            String observaciones = body.get("observaciones");
            if (observaciones == null) return ResponseEntity.badRequest().body("El cuerpo debe contener 'observaciones'");
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
            InformeHlaResponseDTO datos = informeService.getDatosInformeHla(dni);
            return ResponseEntity.ok(Map.of("paciente", datos));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    @PutMapping("/hla/{idHla}/observaciones")
    public ResponseEntity<?> guardarObservacionesHla(@PathVariable Long idHla, @RequestBody Map<String, String> body) {
        try {
            String observaciones = body.get("observaciones");
            if (observaciones == null) return ResponseEntity.badRequest().body("El cuerpo debe contener 'observaciones'");
            informeService.guardarObservacionHla(idHla, observaciones);
            return ResponseEntity.noContent().build();
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
            return ResponseEntity.ok(datos);
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    @PutMapping("/familiar/{idHlaReferencia}/observaciones")
    public ResponseEntity<?> guardarObservacionesFamiliar(@PathVariable Long idHlaReferencia, @RequestBody Map<String, String> body) {
        try {
            String observaciones = body.get("observaciones");
            if (observaciones == null) return ResponseEntity.badRequest().body("El cuerpo debe contener 'observaciones'");
            informeService.guardarObservacionFamiliar(idHlaReferencia, observaciones);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    // ==========================================
    // SECCIÓN DE REPORTES PDF
    // ==========================================

    // 1. PDF INFORME DSA
    @GetMapping("/dsa/pdf")
    public ResponseEntity<byte[]> descargarPdfDsa(@RequestParam String dni) {
        try {
            InformeDsaResponseDTO data = informeService.getDatosInformeDsa(dni);

            Map<String, Object> variables = new HashMap<>();
            Map<String, Object> datosPaciente = new HashMap<>();
            datosPaciente.put("nombre", data.getNombrePaciente());
            datosPaciente.put("dni", data.getDni());
            datosPaciente.put("muestra", data.getMuestra());
            datosPaciente.put("fechaMuestra", data.getFecha());
            datosPaciente.put("medico", "Dr. Solicitante");
            variables.put("datos", datosPaciente);

            variables.put("resultadosClaseI", data.getListaDsaClase1());
            variables.put("resultadosClaseII", data.getListaDsaClase2());
            variables.put("observaciones", data.getObservaciones());

            byte[] pdfBytes = pdfService.generarPdf("informe_dsa", variables);
            return generarResponsePdf(pdfBytes, "informe_dsa_" + dni);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    // 2. PDF INFORME HLA FAMILIAR
    @GetMapping("/familiar/pdf")
    public ResponseEntity<byte[]> descargarPdfFamiliar(@RequestParam String dni) {
        try {
            InformeFamiliarResponseDTO data = informeService.getDatosInformeFamiliar(dni);

            Map<String, Object> variables = new HashMap<>();
            variables.put("fechaActual", LocalDate.now());

            Map<String, Object> datosP = new HashMap<>();
            datosP.put("nombre", data.getPaciente().getNombre());
            datosP.put("dni", data.getPaciente().getDni());
            datosP.put("muestra", data.getPaciente().getMuestra());
            datosP.put("medico", "Dr. Ejemplo");
            datosP.put("hla", mapHlaListsToMap(data.getPaciente()));
            variables.put("datos", datosP);

            List<Map<String, Object>> listaDonantes = new ArrayList<>();
            if (data.getDonantes() != null) {
                for (FamiliarReporteDTO fam : data.getDonantes()) {
                    Map<String, Object> mapFam = new HashMap<>();
                    mapFam.put("nombre", fam.getNombre());
                    mapFam.put("muestra", fam.getMuestra());
                    mapFam.put("vinculo", fam.getVinculo());
                    mapFam.put("hla", mapHlaListsToMap(fam));
                    listaDonantes.add(mapFam);
                }
            }
            variables.put("donantes", listaDonantes);
            variables.put("observaciones", data.getNotaGeneral());

            byte[] pdfBytes = pdfService.generarPdf("informe_familiar", variables);
            return generarResponsePdf(pdfBytes, "informe_familiar_" + dni);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    // 3. PDF INFORME HLA INSCRIPCIÓN (Agregado)
    @GetMapping("/hla/pdf")
    public ResponseEntity<byte[]> descargarPdfHlaInscripcion(@RequestParam String dni) {
        try {
            InformeHlaResponseDTO data = informeService.getDatosInformeHla(dni);

            Map<String, Object> variables = new HashMap<>();
            variables.put("fechaActual", LocalDate.now());
            variables.put("tipoLista", "RENAL");
            variables.put("datos", data);
            variables.put("praGlobal", "0");
            variables.put("praClase1", "0");
            variables.put("praClase2", "0");

            byte[] pdfBytes = pdfService.generarPdf("informe_hla_inscripcion", variables);
            return generarResponsePdf(pdfBytes, "formulario_inscripcion_" + dni);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    // ==========================================
    // MÉTODOS AUXILIARES
    // ==========================================

    // Este es el método que te faltaba
    private ResponseEntity<byte[]> generarResponsePdf(byte[] pdfBytes, String filename) {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=" + filename + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }

    private Map<String, String> mapHlaListsToMap(FamiliarReporteDTO dto) {
        Map<String, String> hla = new HashMap<>();
        List<String> abc = dto.getAbc();
        List<String> drq = dto.getDrq();

        if (abc != null && abc.size() >= 6) {
            hla.put("a1", abc.get(0)); hla.put("a2", abc.get(1));
            hla.put("b1", abc.get(2)); hla.put("b2", abc.get(3));
            hla.put("bw1", abc.get(4)); hla.put("bw2", abc.get(5));
            hla.put("c1", abc.get(4)); hla.put("c2", abc.get(5));
        }
        if (drq != null && drq.size() >= 10) {
            hla.put("dr1", drq.get(0)); hla.put("dr2", drq.get(1));
            hla.put("drLargo1", drq.get(2)); hla.put("drLargo2", drq.get(3));
            hla.put("dqa1", drq.get(2)); hla.put("dqa2", drq.get(3));
            hla.put("dqb1", drq.get(4)); hla.put("dqb2", drq.get(5));
            hla.put("dpa1", drq.get(6)); hla.put("dpa2", drq.get(7));
            hla.put("dpb1", drq.get(8)); hla.put("dpb2", drq.get(9));
        }
        return hla;
    }
}