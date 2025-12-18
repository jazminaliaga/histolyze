package com.histolyze.histolyze.service;

import com.histolyze.histolyze.model.DSA;
import java.util.List;
import java.util.Optional;

public interface DSAService {
    DSA save(DSA dsa);
    List<DSA> findAll();
    Optional<DSA> findById(Long id);
    void delete(Long id);
}
