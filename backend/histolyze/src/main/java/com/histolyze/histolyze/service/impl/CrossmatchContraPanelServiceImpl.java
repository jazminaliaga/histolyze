package com.histolyze.histolyze.service.impl;

import com.histolyze.histolyze.model.CrossmatchContraPanel;
import com.histolyze.histolyze.model.Paciente;
import com.histolyze.histolyze.repository.CrossmatchContraPanelRepository;
import com.histolyze.histolyze.repository.PacienteRepository;
import com.histolyze.histolyze.service.CrossmatchContraPanelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class CrossmatchContraPanelServiceImpl implements CrossmatchContraPanelService {

    private final CrossmatchContraPanelRepository repository;
    @Autowired
    private PacienteRepository pacienteRepository;

    public CrossmatchContraPanelServiceImpl(CrossmatchContraPanelRepository repository) {
        this.repository = repository;
    }

    @Override
    public CrossmatchContraPanel save(CrossmatchContraPanel crossmatch, Long idPaciente) {
        Paciente paciente = pacienteRepository.findById(idPaciente)
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado: " + idPaciente));
        crossmatch.setPaciente(paciente);

        return repository.save(crossmatch);
    }

    @Override
    public CrossmatchContraPanel update(Long idPaciente, Long idCrossmatch, CrossmatchContraPanel crossmatchDetails) {
        // 1. Validar que el paciente exista
        Paciente paciente = pacienteRepository.findById(idPaciente)
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado: " + idPaciente));

        // 2. Encontrar el registro existente
        CrossmatchContraPanel existingCrossmatch = repository.findById(idCrossmatch)
                .orElseThrow(() -> new RuntimeException("Crossmatch no encontrado: " + idCrossmatch));

        // 3. Asegurarse que el registro pertenezca a ese paciente (Opcional pero recomendado)
        if (!existingCrossmatch.getPaciente().getIdPaciente().equals(idPaciente)) {
            throw new RuntimeException("Error: El Crossmatch no pertenece al paciente especificado.");
        }

        // 4. Actualizar los campos con los datos nuevos (del JSON)
        // (Usando los nombres de campo corregidos del modelo)
        existingCrossmatch.setFecha(crossmatchDetails.getFecha());
        existingCrossmatch.setNumeroMuestra(crossmatchDetails.getNumeroMuestra());
        existingCrossmatch.setAntiHla1(crossmatchDetails.getAntiHla1());
        existingCrossmatch.setAntiHla2(crossmatchDetails.getAntiHla2());
        existingCrossmatch.setResultado(crossmatchDetails.getResultado());
        existingCrossmatch.setAnticuerposNoConfirmados(crossmatchDetails.getAnticuerposNoConfirmados());

        // 5. Guardar la entidad actualizada
        return repository.save(existingCrossmatch);
    }

    @Override
    public List<CrossmatchContraPanel> findAll() {
        return repository.findAll();
    }

    @Override
    public Optional<CrossmatchContraPanel> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }

    @Override
    public List<CrossmatchContraPanel> findByPaciente(Long idPaciente) {
        return repository.findByPacienteIdPaciente(idPaciente);
    }
}
