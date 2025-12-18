package com.histolyze.histolyze.controller;

import com.histolyze.histolyze.model.AnticuerpoAntiHLA;
import com.histolyze.histolyze.service.AnticuerpoAntiHLAService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/anticuerpos")
@CrossOrigin(origins = "*")
public class AnticuerpoAntiHLAController {

    private final AnticuerpoAntiHLAService service;

    public AnticuerpoAntiHLAController(AnticuerpoAntiHLAService service) {
        this.service = service;
    }

    @GetMapping
    public List<AnticuerpoAntiHLA> getAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public AnticuerpoAntiHLA getById(@PathVariable Long id) {
        return service.findById(id).orElse(null);
    }

    @GetMapping("/dsa/{idDsa}")
    public List<AnticuerpoAntiHLA> getByDsa(@PathVariable Long idDsa) {
        return service.findByDsa(idDsa);
    }

    @PostMapping
    public AnticuerpoAntiHLA create(@RequestBody AnticuerpoAntiHLA anticuerpo) {
        return service.save(anticuerpo);
    }

    @PutMapping("/{id}")
    public AnticuerpoAntiHLA update(@PathVariable Long id, @RequestBody AnticuerpoAntiHLA anticuerpo) {
        anticuerpo.setIdAnticuerpo(id);
        return service.save(anticuerpo);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
