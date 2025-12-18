package com.histolyze.histolyze.service.impl;

import com.histolyze.histolyze.model.Antecedente;
import com.histolyze.histolyze.model.Paciente;
import com.histolyze.histolyze.model.Usuario;
import com.histolyze.histolyze.repository.UsuarioRepository;
import com.histolyze.histolyze.repository.PacienteRepository;
import com.histolyze.histolyze.service.PacienteService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

@Service
public class PacienteServiceImpl implements PacienteService {

    private final PacienteRepository pacienteRepository;
    private final UsuarioRepository usuarioRepository;

    @Autowired
    public PacienteServiceImpl(PacienteRepository pacienteRepository, UsuarioRepository usuarioRepository) {
        this.pacienteRepository = pacienteRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    @Transactional
    public Paciente guardarPaciente(Paciente paciente) {
        String usuarioDni = SecurityContextHolder.getContext().getAuthentication().getName();

        Usuario usuarioActual = usuarioRepository.findByDni(usuarioDni)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado en el contexto de seguridad"));

        paciente.setCreadoPor(usuarioActual);

        List<Antecedente> antecedentes = paciente.getAntecedentes();

        if (antecedentes != null && !antecedentes.isEmpty()) {
            antecedentes.forEach(antecedente -> {
                antecedente.setPaciente(paciente);
                antecedente.setUsuario(usuarioActual);

                // Iteramos sobre las listas DENTRO de CADA antecedente
                if (antecedente.getListaTransfusiones() != null) {
                    antecedente.getListaTransfusiones().forEach(transfusion -> {
                        transfusion.setAntecedente(antecedente);
                    });
                }
                if (antecedente.getListaTrasplantes() != null) {
                    antecedente.getListaTrasplantes().forEach(trasplante -> {
                        trasplante.setAntecedente(antecedente);
                    });
                }
            });
        }

        if (paciente.getTipificacionesHLA() != null) {
            paciente.getTipificacionesHLA().forEach(hla -> hla.setPaciente(paciente));
        }
        if (paciente.getDsa() != null) {
            paciente.getDsa().forEach(d -> d.setPaciente(paciente));
        }
        if (paciente.getCrossmatchContraPanel() != null) {
            paciente.getCrossmatchContraPanel().forEach(c -> c.setPaciente(paciente));
        }

        return pacienteRepository.save(paciente);
    }

    @Override
    public Optional<Paciente> obtenerPacientePorId(Long id) {
        return pacienteRepository.findById(id);
    }

    @Override
    public Optional<Paciente> obtenerPacientePorDni(String dni) {
        return pacienteRepository.findByDni(dni);
    }

    @Override
    public List<Paciente> listarPacientes() {
        return pacienteRepository.findAll();
    }

    @Override
    public void eliminarPaciente(Long id) {
        pacienteRepository.deleteById(id);
    }
}

