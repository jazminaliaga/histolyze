package com.histolyze.histolyze.dto;

import com.histolyze.histolyze.model.Familiar;
import com.histolyze.histolyze.model.HlaDonante;
import com.histolyze.histolyze.model.TipificacionesHLA;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.List;

@Data
@NoArgsConstructor
public class FamiliarReporteDTO {
    private String nombre;
    private String muestra;
    private String vinculo; // Solo para familiares
    private List<String> abc;
    private List<String> drq;
    private Long idFamiliar; // Para guardar observaciones si decidimos hacerlo por familiar
    private String observaciones; // Campo para observaciones individuales si es necesario

    // Constructor para el Paciente (desde TipificacionesHLA)
    public FamiliarReporteDTO(TipificacionesHLA hlaPaciente) {
        if (hlaPaciente != null && hlaPaciente.getPaciente() != null) {
            this.nombre = hlaPaciente.getPaciente().getNombre() + " " + hlaPaciente.getPaciente().getApellido();
        }
        this.muestra = hlaPaciente != null ? hlaPaciente.getNumeroMuestra() : "N/A";
        this.vinculo = "Paciente"; // Identificador
        this.abc = hlaPaciente != null ? Arrays.asList(
                hlaPaciente.getLocusA01(), hlaPaciente.getLocusA02(),
                hlaPaciente.getLocusB01(), hlaPaciente.getLocusB02(),
                hlaPaciente.getLocusC01(), hlaPaciente.getLocusC02()
        ) : Arrays.asList("-", "-", "-", "-", "-", "-");
        this.drq = hlaPaciente != null ? Arrays.asList(
                hlaPaciente.getLocusDR01(), hlaPaciente.getLocusDR02(),
                hlaPaciente.getLocusDQA01(), hlaPaciente.getLocusDQA02(),
                hlaPaciente.getLocusDQB01(), hlaPaciente.getLocusDQB02(),
                hlaPaciente.getLocusDPA01(), hlaPaciente.getLocusDPA02(),
                hlaPaciente.getLocusDPB01(), hlaPaciente.getLocusDPB02()
        ) : Arrays.asList("-", "-", "-", "-", "-", "-", "-", "-", "-", "-");
    }

    // Constructor para un Familiar (desde la entidad Familiar)
    public FamiliarReporteDTO(Familiar familiar) {
        this.nombre = familiar.getNombre();
        this.muestra = familiar.getNumeroMuestra();
        this.vinculo = familiar.getVinculo() != null ? familiar.getVinculo().name() : "N/A";
        this.idFamiliar = familiar.getIdFamiliar(); // Guardamos el ID
        this.observaciones = familiar.getObservaciones(); // Cargamos observaciones si existen

        HlaDonante hlaData = familiar.getHlaData();
        this.abc = hlaData != null ? Arrays.asList(
                hlaData.getA1(), hlaData.getA2(),
                hlaData.getB1(), hlaData.getB2(),
                hlaData.getC1(), hlaData.getC2()
        ) : Arrays.asList("-", "-", "-", "-", "-", "-");
        this.drq = hlaData != null ? Arrays.asList(
                hlaData.getDr1(), hlaData.getDr2(),
                hlaData.getDqa1_1(), hlaData.getDqa1_2(),
                hlaData.getDqb1_1(), hlaData.getDqb1_2(),
                hlaData.getDpa1_1(), hlaData.getDpa1_2(),
                hlaData.getDpb1_1(), hlaData.getDpb1_2()
        ) : Arrays.asList("-", "-", "-", "-", "-", "-", "-", "-", "-", "-");
    }
}