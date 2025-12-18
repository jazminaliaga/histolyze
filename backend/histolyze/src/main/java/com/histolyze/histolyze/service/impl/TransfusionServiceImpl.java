package com.histolyze.histolyze.service.impl;

import com.histolyze.histolyze.model.Transfusion;
import com.histolyze.histolyze.repository.TransfusionRepository;
import com.histolyze.histolyze.service.TransfusionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class TransfusionServiceImpl implements TransfusionService {

    @Autowired
    private TransfusionRepository transfusionRepository;

    @Override
    public List<Transfusion> listarTodas() {
        return transfusionRepository.findAll();
    }

    @Override
    public Optional<Transfusion> buscarPorId(Long id) {
        return transfusionRepository.findById(id);
    }

    @Override
    public Transfusion guardar(Transfusion transfusion) {
        return transfusionRepository.save(transfusion);
    }

    @Override
    public void eliminar(Long id) {
        transfusionRepository.deleteById(id);
    }
}
