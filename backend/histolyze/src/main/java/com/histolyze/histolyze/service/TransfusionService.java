package com.histolyze.histolyze.service;

import com.histolyze.histolyze.model.Transfusion;
import java.util.List;
import java.util.Optional;

public interface TransfusionService {

    List<Transfusion> listarTodas();
    Optional<Transfusion> buscarPorId(Long id);
    Transfusion guardar(Transfusion transfusion);
    void eliminar(Long id);
}