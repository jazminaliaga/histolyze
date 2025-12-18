package com.histolyze.histolyze.service;

import com.histolyze.histolyze.model.AnticuerpoAntiHLA;
import com.histolyze.histolyze.model.AnticuerpoAntiHLA.TipoClase;
import com.histolyze.histolyze.model.DSA;
import com.histolyze.histolyze.model.Paciente;
import com.histolyze.histolyze.repository.AnticuerpoAntiHLARepository;
import com.histolyze.histolyze.repository.DSARepository;
import com.histolyze.histolyze.repository.PacienteRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ReportParsingService {

    @Autowired
    private PacienteRepository pacienteRepository;

    @Autowired
    private DSARepository dsaRepository;

    @Autowired
    private AnticuerpoAntiHLARepository anticuerpoAntiHLARepository;

    private final DataFormatter dataFormatter = new DataFormatter();

    @Transactional
    public DSA parseAndSaveDsaReport(MultipartFile file) throws Exception {

        try (InputStream is = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(is)) {

            Sheet sheet = workbook.getSheetAt(0);

            // --- 1. Parsear el Encabezado ---
            String patientDni = null;
            LocalDate testDate = null;
            String numeroMuestra = null;

            Row row1 = sheet.getRow(1);
            if (row1 != null && dataFormatter.formatCellValue(row1.getCell(0)).equals("Sample ID:")) {
                patientDni = dataFormatter.formatCellValue(row1.getCell(1)).trim();
                numeroMuestra = patientDni;
            } else {
                throw new Exception("Formato de Excel no válido: No se encontró 'Sample ID:' en la Fila 1");
            }

            Row row2 = sheet.getRow(2);
            if (row2 != null && dataFormatter.formatCellValue(row2.getCell(4)).equals("Test Date:")) {
                String dateStr = dataFormatter.formatCellValue(row2.getCell(5));
                testDate = parseDate(dateStr);
            } else {
                throw new Exception("Formato de Excel no válido: No se encontró 'Test Date:' en la Fila 2");
            }

            // --- 2. Buscar al Paciente ---
            Optional<Paciente> pacienteOptional = pacienteRepository.findByDni(patientDni);

            if (pacienteOptional.isEmpty()) {
                throw new Exception("El paciente con DNI " + patientDni + " no existe en la base de datos.");
            }
            Paciente paciente = pacienteOptional.get();

            // --- 3. Crear y Guardar el DSA (el "Padre") ---
            DSA dsa = new DSA();
            dsa.setFecha(testDate);
            dsa.setDniPaciente(paciente.getDni());
            dsa.setNombrePaciente(paciente.getNombre() + " " + paciente.getApellido());
            dsa.setMedicoSolicitante(paciente.getMedicoSolicitante());
            dsa.setNumeroMuestra(numeroMuestra);
            dsa.setPaciente(paciente);

            DSA savedDsa = dsaRepository.save(dsa);

            // --- 4. Parsear los Anticuerpos (los "Hijos") ---
            List<AnticuerpoAntiHLA> anticuerposList = new ArrayList<>();
            boolean inDataTable = false;

            for (Row row : sheet) {
                String cell0 = dataFormatter.formatCellValue(row.getCell(0)).trim();

                if (cell0.equals("Test Details")) {
                    inDataTable = true;
                    continue;
                }

                if (inDataTable) {
                    if (cell0.isEmpty() || cell0.equals("Patient/Donor:") || row.getRowNum() > 208) {

                        if(cell0.equals("Patient/Donor:")) {
                            if (!anticuerposList.isEmpty()) {
                                anticuerpoAntiHLARepository.saveAll(anticuerposList);
                                anticuerposList.clear();
                            }
                        }

                        if (cell0.isEmpty() && row.getRowNum() > 208) {
                            inDataTable = false;
                        }

                        continue;
                    }

                    if (cell0.equals("001") || cell0.equals("002")) {
                        continue;
                    }

                    // --- INICIO DE LA CORRECCIÓN ---
                    // Fila 5: [ Test Details(0) | Bead(1) | Rxn(2) | Raw(3) | SFI Raw(4) | (5) | Normal(6) | SFI Normal(7) | Cnt(8) | Specificity(9) | Allele Specificity(10) | ... ]
                    // Tu Fila 10: [ ... | 9.95(4) | ... | 128(7) | | A2(8) | A*02:03(9) | ... ]

                    // Serológico (A2) está en la celda 8
                    String specificity = dataFormatter.formatCellValue(row.getCell(8));

                    // Alélico (A*02:03) está en la celda 9
                    String alleleSpecificity = dataFormatter.formatCellValue(row.getCell(9));

                    // MFI (9.95) es SFI Raw, está en la celda 4
                    Integer mfi = parseInteger(dataFormatter.formatCellValue(row.getCell(4)));

                    // --- FIN DE LA CORRECCIÓN ---

                    if (specificity.isEmpty()) {
                        continue;
                    }

                    AnticuerpoAntiHLA ac = new AnticuerpoAntiHLA();
                    ac.setSerologico(specificity);
                    ac.setAlelico(alleleSpecificity.isEmpty() ? specificity : alleleSpecificity); // Si alelico está vacío, usa serológico
                    ac.setMfi(mfi); // Usamos el MFI corregido

                    String resultado = "negativo"; // Valor por defecto
                    if (mfi != null && mfi > 1000) {
                        resultado = "positivo";
                    }
                    ac.setResultado(resultado);

                    ac.setTipo(getTipoClase(specificity));
                    ac.setDsa(savedDsa);

                    anticuerposList.add(ac);
                }
            }

            // --- 5. Guardar los Anticuerpos (Clase 2) ---
            if (!anticuerposList.isEmpty()) {
                anticuerpoAntiHLARepository.saveAll(anticuerposList);
            }

            return savedDsa;

        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Error al parsear el archivo: " + e.getMessage());
        }
    }

    // --- Métodos de Ayuda (Utilities) ---

    private LocalDate parseDate(String str) {
        if (str == null || str.trim().isEmpty()) return null;
        String cleanDate = str.split("\"")[0].trim();
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/M/yyyy");
            return LocalDate.parse(cleanDate, formatter);
        } catch (Exception e) {
            System.err.println("Error al parsear fecha: " + str);
            return null;
        }
    }

    private Integer parseInteger(String str) {
        if (str == null || str.trim().isEmpty()) return null;
        try {
            // 1. Parsear el string a Double (ej: "9.95")
            Double doubleValue = Double.valueOf(str.trim());

            // 2. Redondear el Double al 'long' más cercano (ej: 9.95 -> 10)
            long roundedValue = Math.round(doubleValue);

            // 3. Convertir el long a Integer
            return (int) roundedValue;

        } catch (NumberFormatException e) {
            return null;
        }
    }

    private TipoClase getTipoClase(String specificity) {
        if (specificity.startsWith("A") || specificity.startsWith("B") || specificity.startsWith("C")) {
            return TipoClase.Clase1;
        }
        else if (specificity.startsWith("DR") || specificity.startsWith("DQ") || specificity.startsWith("DP")) {
            return TipoClase.Clase2;
        }
        return TipoClase.Clase1; // Fallback
    }
}