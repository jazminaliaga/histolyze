package com.histolyze.histolyze.service;

import com.histolyze.histolyze.dto.*;
public interface InformeService {

    InformeDsaResponseDTO getDatosInformeDsa(String dni);

    void guardarObservacionDsa(Long idDsa, String observaciones);

    InformeHlaResponseDTO getDatosInformeHla(String dni);

    void guardarObservacionHla(Long idHla, String observaciones);

    InformeFamiliarResponseDTO getDatosInformeFamiliar(String dni);

    void guardarObservacionFamiliar(Long idHlaReferencia, String observaciones);
}