package com.histolyze.histolyze.dto;

import com.histolyze.histolyze.model.AnticuerpoAntiHLA;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InformeDsaResponseDTO {

    // --- Datos del Encabezado (Nuevos) ---
    private String nombrePaciente;
    private String dni;
    private String muestra;
    private LocalDate fecha; // Fecha de muestra
    private String observaciones;

    // --- Datos existentes ---
    private DsaSimpleDTO dsa; // (Lo mantenemos por compatibilidad, aunque quizás no lo uses en el PDF)
    private List<AnticuerpoAntiHLA> anticuerpos;

    // --- Métodos Helper para el Controller/Thymeleaf ---

    // Filtra la lista para devolver solo Clase 1
    public List<AnticuerpoAntiHLA> getListaDsaClase1() {
        if (anticuerpos == null) return new ArrayList<>();
        return anticuerpos.stream()
                .filter(a -> a.getTipo() == AnticuerpoAntiHLA.TipoClase.Clase1)
                .collect(Collectors.toList());
    }

    // Filtra la lista para devolver solo Clase 2
    public List<AnticuerpoAntiHLA> getListaDsaClase2() {
        if (anticuerpos == null) return new ArrayList<>();
        return anticuerpos.stream()
                .filter(a -> a.getTipo() == AnticuerpoAntiHLA.TipoClase.Clase2)
                .collect(Collectors.toList());
    }
}