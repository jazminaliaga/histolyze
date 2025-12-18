package com.histolyze.histolyze.dto;

import com.histolyze.histolyze.model.DSA;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DsaSimpleDTO {
    private String locus;
    private String serologico;
    private String alelico;
    private Integer mfi;
    private String resultado;

}