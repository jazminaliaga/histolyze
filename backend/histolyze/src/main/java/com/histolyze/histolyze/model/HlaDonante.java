package com.histolyze.histolyze.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
public class HlaDonante {

    // Clase I
    @Column(name = "hla_donante_a1")
    private String a1;
    @Column(name = "hla_donante_a2")
    private String a2;
    @Column(name = "hla_donante_b1")
    private String b1;
    @Column(name = "hla_donante_b2")
    private String b2;
    @Column(name = "hla_donante_c1")
    private String c1;
    @Column(name = "hla_donante_c2")
    private String c2;

    // Clase II
    @Column(name = "hla_donante_dr1")
    private String dr1;
    @Column(name = "hla_donante_dr2")
    private String dr2;
    @Column(name = "hla_donante_dqa1_1")
    private String dqa1_1;
    @Column(name = "hla_donante_dqa1_2")
    private String dqa1_2;
    @Column(name = "hla_donante_dqb1_1")
    private String dqb1_1;
    @Column(name = "hla_donante_dqb1_2")
    private String dqb1_2;
    @Column(name = "hla_donante_dpa1_1")
    private String dpa1_1;
    @Column(name = "hla_donante_dpa1_2")
    private String dpa1_2;
    @Column(name = "hla_donante_dpb1_1")
    private String dpb1_1;
    @Column(name = "hla_donante_dpb1_2")
    private String dpb1_2;
}