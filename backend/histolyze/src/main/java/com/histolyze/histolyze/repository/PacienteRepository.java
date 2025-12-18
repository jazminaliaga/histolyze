package com.histolyze.histolyze.repository;

import com.histolyze.histolyze.model.Paciente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PacienteRepository extends JpaRepository<Paciente, Long> {

    Optional<Paciente> findByDni(String dni);

    Optional<Paciente> findByDniOrNumeroMuestra(String dni, String numeroMuestra);
}
