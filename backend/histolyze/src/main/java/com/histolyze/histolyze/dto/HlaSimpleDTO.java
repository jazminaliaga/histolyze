package com.histolyze.histolyze.dto;

import com.histolyze.histolyze.model.TipificacionesHLA;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class HlaSimpleDTO {
    // Clase I
    private String a1;
    private String a2;
    private String b1;
    private String b2;
    private String bw1; // Nota: No tenemos 'Bw' en la BD, se omitirá
    private String bw2; // Nota: No tenemos 'Bw' en la BD, se omitirá
    private String c1;
    private String c2;

    // Clase II
    private String dr1;
    private String dr2;
    private String dq1; // Usaremos DQB1 para este campo
    private String dq2; // Usaremos DQB2 para este campo
    private String dp1; // Usaremos DPB1 para este campo
    private String dp2; // Usaremos DPB2 para este campo

    // Constructor para mapear desde la entidad
    public HlaSimpleDTO(TipificacionesHLA hla) {
        this.a1 = hla.getLocusA01();
        this.a2 = hla.getLocusA02();
        this.b1 = hla.getLocusB01();
        this.b2 = hla.getLocusB02();
        this.c1 = hla.getLocusC01();
        this.c2 = hla.getLocusC02();
        this.dr1 = hla.getLocusDR01();
        this.dr2 = hla.getLocusDR02();
        this.dq1 = hla.getLocusDQB01(); // Mapeo de DQB
        this.dq2 = hla.getLocusDQB02();
        this.dp1 = hla.getLocusDPB01(); // Mapeo de DPB
        this.dp2 = hla.getLocusDPB02();
        // Bw no existe en la entidad, por lo que bw1 y bw2 quedarán en null
    }
}