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
    private String bw1;
    private String bw2;
    private String c1;
    private String c2;

    // Clase II
    private String dr1;
    private String dr2;
    private String drLargo1;
    private String drLargo2;
    private String dqa1;
    private String dqa2;
    private String dpa1;
    private String dpa2;
    private String dqb1;
    private String dqb2;
    private String dpb1;
    private String dpb2;

    // Constructor para mapear desde la entidad
    public HlaSimpleDTO(TipificacionesHLA hla) {
        this.a1 = hla.getLocusA01();
        this.a2 = hla.getLocusA02();
        this.b1 = hla.getLocusB01();
        this.b2 = hla.getLocusB02();
        this.bw1 = hla.getLocusBw01();
        this.bw2 = hla.getLocusBw02();
        this.c1 = hla.getLocusC01();
        this.c2 = hla.getLocusC02();
        this.dr1 = hla.getLocusDR01();
        this.dr2 = hla.getLocusDR02();
        this.drLargo1 = hla.getLocusDrLargo01();
        this.drLargo2 = hla.getLocusDrLargo02();
        this.dqa1 = hla.getLocusDQA01();
        this.dqa2 = hla.getLocusDQA02();
        this.dqb1 = hla.getLocusDQB01();
        this.dqb2 = hla.getLocusDQB02();
        this.dpa1 = hla.getLocusDPA01();
        this.dpa2 = hla.getLocusDPA02();
        this.dpb1 = hla.getLocusDPB01();
        this.dpb2 = hla.getLocusDPB02();
    }
}