package com.histolyze.histolyze.controller;

import com.histolyze.histolyze.model.Antecedente;
import com.histolyze.histolyze.service.AntecedenteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/antecedentes")
@CrossOrigin(origins = "http://127.0.0.1:5500")
public class AntecedenteController {

    @Autowired
    private AntecedenteService antecedenteService;

    @GetMapping
    public List<Antecedente> listar() {
        return antecedenteService.listarTodos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Antecedente> buscarPorId(@PathVariable Long id) {
        return antecedenteService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Antecedente guardar(@RequestBody Antecedente antecedente) {
        return antecedenteService.guardar(antecedente);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        antecedenteService.eliminar(id);
    }
}
