package com.histolyze.histolyze.repository;

import com.histolyze.histolyze.model.Familiar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FamiliarRepository extends JpaRepository<Familiar, Long> {

    Optional<Familiar> findByDni(String dni);
    Optional<Familiar> findByNumeroMuestra(String numeroMuestra);
}