package com.histolyze.histolyze.controller;

import com.histolyze.histolyze.model.Transfusion;
import com.histolyze.histolyze.service.TransfusionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transfusiones")
@CrossOrigin(origins = "http://127.0.0.1:5500")
public class TransfusionController {

    @Autowired
    private TransfusionService transfusionService;

    @GetMapping
    public List<Transfusion> listar() {
        return transfusionService.listarTodas();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Transfusion> buscarPorId(@PathVariable Long id) {
        return transfusionService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Transfusion guardar(@RequestBody Transfusion transfusion) {
        return transfusionService.guardar(transfusion);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        transfusionService.eliminar(id);
    }
}
