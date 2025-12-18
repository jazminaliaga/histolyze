package com.histolyze.histolyze.service;

import com.histolyze.histolyze.model.Antecedente;
import java.util.List;
import java.util.Optional;

public interface AntecedenteService {

    List<Antecedente> listarTodos();
    Optional<Antecedente> buscarPorId(Long id);
    Antecedente guardar(Antecedente antecedente);
    void eliminar(Long id);
}
