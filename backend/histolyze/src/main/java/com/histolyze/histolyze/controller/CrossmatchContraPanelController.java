package com.histolyze.histolyze.controller;

import com.histolyze.histolyze.model.CrossmatchContraPanel;
import com.histolyze.histolyze.service.CrossmatchContraPanelService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class CrossmatchContraPanelController {

    private final CrossmatchContraPanelService service;

    public CrossmatchContraPanelController(CrossmatchContraPanelService service) {
        this.service = service;
    }

    @GetMapping
    public List<CrossmatchContraPanel> getAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public CrossmatchContraPanel getById(@PathVariable Long id) {
        return service.findById(id).orElse(null);
    }

    @GetMapping("/pacientes/{idPaciente}/crossmatch")
    public List<CrossmatchContraPanel> getByPaciente(@PathVariable Long idPaciente) {
        return service.findByPaciente(idPaciente);
    }

    @PostMapping("/pacientes/{idPaciente}/crossmatch")
    public ResponseEntity<CrossmatchContraPanel> create(
            @PathVariable Long idPaciente,
            @RequestBody CrossmatchContraPanel crossmatch) {

        // Llama al método 'save' que SÍ recibe el idPaciente
        CrossmatchContraPanel nuevoCrossmatch = service.save(crossmatch, idPaciente);
        return new ResponseEntity<>(nuevoCrossmatch, HttpStatus.CREATED);
    }

    @PutMapping("/pacientes/{idPaciente}/crossmatch/{id}")
    public ResponseEntity<CrossmatchContraPanel> update(
            @PathVariable Long idPaciente,
            @PathVariable Long id,
            @RequestBody CrossmatchContraPanel crossmatchDetails) {

        CrossmatchContraPanel updatedCrossmatch = service.update(idPaciente, id, crossmatchDetails);
        return ResponseEntity.ok(updatedCrossmatch);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
