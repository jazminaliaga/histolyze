package com.histolyze.histolyze.service.impl;

import com.histolyze.histolyze.model.AnticuerpoAntiHLA;
import com.histolyze.histolyze.model.DSA;
import com.histolyze.histolyze.repository.AnticuerpoAntiHLARepository;
import com.histolyze.histolyze.repository.DSARepository;
import com.histolyze.histolyze.service.AnticuerpoAntiHLAService;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class AnticuerpoAntiHLAServiceImpl implements AnticuerpoAntiHLAService {

    private final AnticuerpoAntiHLARepository repository;
    private final DSARepository dsaRepository;

    public AnticuerpoAntiHLAServiceImpl(AnticuerpoAntiHLARepository repository, DSARepository dsaRepository) {
        this.repository = repository;
        this.dsaRepository = dsaRepository;
    }

    @Override
    public AnticuerpoAntiHLA save(AnticuerpoAntiHLA anticuerpo) {
        return repository.save(anticuerpo);
    }

    @Override
    public List<AnticuerpoAntiHLA> findAll() {
        return repository.findAll();
    }

    @Override
    public Optional<AnticuerpoAntiHLA> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }

    @Override
    public List<AnticuerpoAntiHLA> findByDsa(Long idDsa) {
        // 1. Primero, buscamos el objeto DSA completo usando el ID
        DSA dsa = dsaRepository.findById(idDsa)
                .orElseThrow(() -> new RuntimeException("DSA no encontrado con ID: " + idDsa));

        // 2. Ahora sí, llamamos al repositorio de anticuerpos con el OBJETO
        return repository.findByDsa(dsa);
    }
}
