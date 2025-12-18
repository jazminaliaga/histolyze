package com.histolyze.histolyze.repository;

import com.histolyze.histolyze.model.Antecedente;
import com.histolyze.histolyze.model.Paciente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AntecedenteRepository extends JpaRepository<Antecedente, Long> {
    List<Antecedente> findByPacienteOrderByIdAntecedenteDesc(Paciente paciente);
}
