package com.histolyze.histolyze.service.impl;


import com.histolyze.histolyze.model.Paciente;
import com.histolyze.histolyze.model.TipificacionesHLA;
import com.histolyze.histolyze.repository.PacienteRepository;
import com.histolyze.histolyze.repository.TipificacionesHLARepository;
import com.histolyze.histolyze.service.TipificacionesHLAService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TipificacionesHLAServiceImpl implements TipificacionesHLAService {

    @Autowired
    private TipificacionesHLARepository repository;

    @Autowired
    private PacienteRepository pacienteRepository;

    @Override
    public List<TipificacionesHLA> listarTipificaciones() {
        return repository.findAll();
    }

    @Override
    public Optional<TipificacionesHLA> obtenerPorId(Long id) {
        return repository.findById(id); //
    }

    @Override
    public TipificacionesHLA guardarTipificacion(TipificacionesHLA tipificacion, Long idPaciente) {
        // 1. Buscar al paciente
        Paciente paciente = pacienteRepository.findById(idPaciente) //
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado con id: " + idPaciente));

        // 2. Asignar el paciente a la tipificación
        tipificacion.setPaciente(paciente);

        // 3. Guardar
        return repository.save(tipificacion);
    }

    @Override
    public TipificacionesHLA actualizarTipificacion(Long idHla, TipificacionesHLA tipificacion, Long idPaciente) {
        Paciente paciente = pacienteRepository.findById(idPaciente) //
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado: " + idPaciente));
        tipificacion.setIdHla(idHla);
        tipificacion.setPaciente(paciente);
        return repository.save(tipificacion);
    }

    @Override
    public void eliminarTipificacion(Long id) {
        repository.deleteById(id);
    }

    @Override
    public List<TipificacionesHLA> findByPaciente(Long idPaciente) {
        // Esta llamada ahora coincide con el Repositorio
        return repository.findByPacienteIdPaciente(idPaciente);
    }
}