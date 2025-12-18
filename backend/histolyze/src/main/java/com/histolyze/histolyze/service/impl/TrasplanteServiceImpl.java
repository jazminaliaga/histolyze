package com.histolyze.histolyze.service.impl;

import com.histolyze.histolyze.model.Trasplante;
import com.histolyze.histolyze.repository.TrasplanteRepository;
import com.histolyze.histolyze.service.TrasplanteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TrasplanteServiceImpl implements TrasplanteService {

    @Autowired
    private TrasplanteRepository trasplanteRepository;

    @Override
    public List<Trasplante> listarTodos() {
        return trasplanteRepository.findAll();
    }

    @Override
    public Optional<Trasplante> buscarPorId(Long id) {
        return trasplanteRepository.findById(id);
    }

    @Override
    public Trasplante guardar(Trasplante trasplante) {
        return trasplanteRepository.save(trasplante);
    }

    @Override
    public void eliminar(Long id) {
        trasplanteRepository.deleteById(id);
    }
}
