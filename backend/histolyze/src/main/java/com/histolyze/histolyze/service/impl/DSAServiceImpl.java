package com.histolyze.histolyze.service.impl;

import com.histolyze.histolyze.model.DSA;
import com.histolyze.histolyze.repository.DSARepository;
import com.histolyze.histolyze.service.DSAService;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class DSAServiceImpl implements DSAService {

    private final DSARepository dsaRepository;

    public DSAServiceImpl(DSARepository dsaRepository) {
        this.dsaRepository = dsaRepository;
    }

    @Override
    public DSA save(DSA dsa) {
        return dsaRepository.save(dsa);
    }

    @Override
    public List<DSA> findAll() {
        return dsaRepository.findAll();
    }

    @Override
    public Optional<DSA> findById(Long id) {
        return dsaRepository.findById(id);
    }

    @Override
    public void delete(Long id) {
        dsaRepository.deleteById(id);
    }
}
