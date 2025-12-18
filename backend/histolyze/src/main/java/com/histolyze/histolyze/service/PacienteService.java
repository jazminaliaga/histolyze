package com.histolyze.histolyze.service;

import com.histolyze.histolyze.model.Paciente;

import java.util.List;
import java.util.Optional;

public interface PacienteService {

    Paciente guardarPaciente(Paciente paciente);

    Optional<Paciente> obtenerPacientePorId(Long id);

    Optional<Paciente> obtenerPacientePorDni(String dni);

    List<Paciente> listarPacientes();

    void eliminarPaciente(Long id);
}
