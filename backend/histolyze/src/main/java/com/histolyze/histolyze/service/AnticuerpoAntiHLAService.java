package com.histolyze.histolyze.service;

import com.histolyze.histolyze.model.AnticuerpoAntiHLA;
import java.util.List;
import java.util.Optional;

public interface AnticuerpoAntiHLAService {
    AnticuerpoAntiHLA save(AnticuerpoAntiHLA anticuerpo);
    List<AnticuerpoAntiHLA> findAll();
    Optional<AnticuerpoAntiHLA> findById(Long id);
    void delete(Long id);
    List<AnticuerpoAntiHLA> findByDsa(Long idDsa);
}
