package com.histolyze.histolyze.controller;

import com.histolyze.histolyze.model.Trasplante;
import com.histolyze.histolyze.service.TrasplanteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api//trasplantes")
@CrossOrigin(origins = "http://127.0.0.1:5500")
public class TrasplanteController {

    @Autowired
    private TrasplanteService trasplanteService;

    @GetMapping
    public List<Trasplante> listar() {
        return trasplanteService.listarTodos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Trasplante> buscarPorId(@PathVariable Long id) {
        return trasplanteService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Trasplante guardar(@RequestBody Trasplante trasplante) {
        return trasplanteService.guardar(trasplante);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        trasplanteService.eliminar(id);
    }
}
