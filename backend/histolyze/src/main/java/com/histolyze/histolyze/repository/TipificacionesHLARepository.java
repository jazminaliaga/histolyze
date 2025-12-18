package com.histolyze.histolyze.repository;

import com.histolyze.histolyze.model.Paciente;
import com.histolyze.histolyze.model.TipificacionesHLA;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TipificacionesHLARepository extends JpaRepository<TipificacionesHLA, Long> {

    List<TipificacionesHLA> findByPacienteOrderByFechaRegistroDesc(Paciente paciente);

    List<TipificacionesHLA> findByPacienteIdPaciente(Long idPaciente);
}

