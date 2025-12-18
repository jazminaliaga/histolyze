package com.histolyze.histolyze.controller;

import com.histolyze.histolyze.model.DSA;
import com.histolyze.histolyze.service.DSAService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dsa")
@CrossOrigin(origins = "*")
public class DSAController {

    private final DSAService dsaService;

    public DSAController(DSAService dsaService) {
        this.dsaService = dsaService;
    }

    @GetMapping
    public List<DSA> getAll() {
        return dsaService.findAll();
    }

    @GetMapping("/{id}")
    public DSA getById(@PathVariable Long id) {
        return dsaService.findById(id).orElse(null);
    }

    @PostMapping
    public DSA create(@RequestBody DSA dsa) {
        return dsaService.save(dsa);
    }

    @PutMapping("/{id}")
    public DSA update(@PathVariable Long id, @RequestBody DSA dsa) {
        dsa.setIdDsa(id);
        return dsaService.save(dsa);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        dsaService.delete(id);
    }
}
