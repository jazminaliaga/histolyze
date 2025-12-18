package com.histolyze.histolyze.controller;

import com.histolyze.histolyze.model.Paciente;
import com.histolyze.histolyze.service.PacienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "http://127.0.0.1:5500")
@RestController
@RequestMapping("/api/pacientes")
public class PacienteController {

    private final PacienteService pacienteService;

    @Autowired
    public PacienteController(PacienteService pacienteService) {
        this.pacienteService = pacienteService;
    }

    // Listar todos los pacientes
    @GetMapping
    public List<Paciente> listarPacientes() {
        return pacienteService.listarPacientes();
    }

    // Obtener paciente por ID
    @GetMapping("/{id}")
    public ResponseEntity<Paciente> obtenerPacientePorId(@PathVariable Long id) {
        Optional<Paciente> pacienteOpt = pacienteService.obtenerPacientePorId(id);
        return pacienteOpt.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Obtener paciente por DNI
    @GetMapping("/dni/{dni}")
    public ResponseEntity<Paciente> obtenerPacientePorDni(@PathVariable String dni) {
        Optional<Paciente> pacienteOpt = pacienteService.obtenerPacientePorDni(dni);
        return pacienteOpt.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Crear nuevo paciente
    @PostMapping
    public Paciente crearPaciente(@RequestBody Paciente paciente) {
        return pacienteService.guardarPaciente(paciente);
    }

    // Actualizar paciente existente
    @PutMapping("/{id}")
    public ResponseEntity<Paciente> actualizarPaciente(@PathVariable Long id, @RequestBody Paciente paciente) {
        Optional<Paciente> pacienteOpt = pacienteService.obtenerPacientePorId(id);
        if (pacienteOpt.isPresent()) {
            paciente.setIdPaciente(id);
            return ResponseEntity.ok(pacienteService.guardarPaciente(paciente));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Eliminar paciente
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarPaciente(@PathVariable Long id) {
        Optional<Paciente> pacienteOpt = pacienteService.obtenerPacientePorId(id);
        if (pacienteOpt.isPresent()) {
            pacienteService.eliminarPaciente(id);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
