package com.histolyze.histolyze.repository;

import com.histolyze.histolyze.model.CrossmatchContraPanel;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CrossmatchContraPanelRepository extends JpaRepository<CrossmatchContraPanel, Long> {
    List<CrossmatchContraPanel> findByPacienteIdPaciente(Long idPaciente);
}
