package com.histolyze.histolyze.service.impl;

import com.histolyze.histolyze.model.Antecedente;
import com.histolyze.histolyze.repository.AntecedenteRepository;
import com.histolyze.histolyze.service.AntecedenteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AntecedenteServiceImpl implements AntecedenteService {

    @Autowired
    private AntecedenteRepository antecedenteRepository;

    @Override
    public List<Antecedente> listarTodos() {
        return antecedenteRepository.findAll();
    }

    @Override
    public Optional<Antecedente> buscarPorId(Long id) {
        return antecedenteRepository.findById(id);
    }

    @Override
    public Antecedente guardar(Antecedente antecedente) {
        return antecedenteRepository.save(antecedente);
    }

    @Override
    public void eliminar(Long id) {
        antecedenteRepository.deleteById(id);
    }
}
