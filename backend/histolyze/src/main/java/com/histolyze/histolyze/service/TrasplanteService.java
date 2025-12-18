package com.histolyze.histolyze.service;

import com.histolyze.histolyze.model.Trasplante;
import java.util.List;
import java.util.Optional;

public interface TrasplanteService {

    List<Trasplante> listarTodos();
    Optional<Trasplante> buscarPorId(Long id);
    Trasplante guardar(Trasplante trasplante);
    void eliminar(Long id);
}
