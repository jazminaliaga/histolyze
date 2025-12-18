package com.histolyze.histolyze.controller;

import com.histolyze.histolyze.model.TipificacionesHLA;
import com.histolyze.histolyze.service.TipificacionesHLAService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:4200")
public class TipificacionesHLAController {

    @Autowired
    private TipificacionesHLAService service;

    @PostMapping("/pacientes/{idPaciente}/hla")
    public ResponseEntity<TipificacionesHLA> guardarTipificacion(
            @PathVariable Long idPaciente,
            @RequestBody TipificacionesHLA tipificacion) {

        return ResponseEntity.ok(service.guardarTipificacion(tipificacion, idPaciente));
    }

    @GetMapping("/pacientes/{idPaciente}/hla")
    public ResponseEntity<List<TipificacionesHLA>> listarTipificacionesPorPaciente(
            @PathVariable Long idPaciente) {

        return ResponseEntity.ok(service.findByPaciente(idPaciente));
    }

    @PutMapping("/pacientes/{idPaciente}/hla/{idHla}")
    public ResponseEntity<TipificacionesHLA> actualizarTipificacion(
            @PathVariable Long idPaciente,
            @PathVariable Long idHla,
            @RequestBody TipificacionesHLA tipificacion) {

        return ResponseEntity.ok(service.actualizarTipificacion(idHla, tipificacion, idPaciente));
    }

    @GetMapping("/hla/{id}")
    public ResponseEntity<TipificacionesHLA> obtenerPorId(@PathVariable Long id) {
        return service.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/hla/{id}")
    public ResponseEntity<Void> eliminarTipificacion(@PathVariable Long id) {
        service.eliminarTipificacion(id);
        return ResponseEntity.noContent().build();
    }
}
