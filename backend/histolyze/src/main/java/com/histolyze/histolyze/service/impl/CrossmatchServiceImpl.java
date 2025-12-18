package com.histolyze.histolyze.service.impl;

import com.histolyze.histolyze.model.Familiar;
import com.histolyze.histolyze.model.Paciente;
import com.histolyze.histolyze.model.DSA;
import com.histolyze.histolyze.model.TipificacionesHLA;
import com.histolyze.histolyze.dto.DsaSimpleDTO;
import com.histolyze.histolyze.dto.HLADonanteDTO;
import com.histolyze.histolyze.repository.DSARepository;
import com.histolyze.histolyze.repository.FamiliarRepository;
import com.histolyze.histolyze.repository.PacienteRepository;
import com.histolyze.histolyze.service.CrossmatchService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

@Service
public class CrossmatchServiceImpl implements CrossmatchService {

    private final PacienteRepository pacienteRepository;
    private final DSARepository dsaRepository;
    private final FamiliarRepository familiarRepository;

    public CrossmatchServiceImpl(PacienteRepository pacienteRepository, DSARepository dsaRepository, FamiliarRepository familiarRepository) {
        this.pacienteRepository = pacienteRepository;
        this.dsaRepository = dsaRepository;
        this.familiarRepository = familiarRepository;
    }

    private Paciente findPacienteByIdentifier(String identifier) {
        return pacienteRepository.findByDniOrNumeroMuestra(identifier, identifier)
                .orElseThrow(() -> new RuntimeException("Paciente receptor no encontrado con DNI/Muestra: " + identifier));
    }

    @Override
    public List<DsaSimpleDTO> getLatestDSAResults(String patientIdentifier) {
        Paciente paciente = findPacienteByIdentifier(patientIdentifier);
        List<DSA> dsaList = dsaRepository.findByPacienteOrderByFechaDesc(paciente);

        if (dsaList.isEmpty()) {
            return List.of();
        }

        DSA ultimoDsa = dsaList.get(0);

        if (ultimoDsa.getAnticuerpos() == null || ultimoDsa.getAnticuerpos().isEmpty()) {
            return List.of();
        }

        return ultimoDsa.getAnticuerpos().stream()
                .filter(a -> a.getSerologico() != null && !a.getSerologico().trim().isEmpty())
                .map(anticuerpo -> new DsaSimpleDTO(
                        anticuerpo.getLocus(),
                        anticuerpo.getSerologico().toUpperCase(),
                        anticuerpo.getAlelico(),
                        anticuerpo.getMfi(),
                        anticuerpo.getResultado()
                ))
                .collect(Collectors.toList());
    }

    @Override
    public HLADonanteDTO getDonanteHLA(String donanteId) {
        TipificacionesHLA hlaData = null;
        String nombreDonante = "Donante Desconocido";

        // 1. BUSCAR EN PACIENTES
        Optional<Paciente> pacienteOpt = pacienteRepository.findByDniOrNumeroMuestra(donanteId, donanteId);
        if (pacienteOpt.isPresent()) {
            Paciente donante = pacienteOpt.get();
            nombreDonante = donante.getNombre() + " " + donante.getApellido() + " (Paciente)";
            if (donante.getTipificacionesHLA() != null && !donante.getTipificacionesHLA().isEmpty()) {
                hlaData = donante.getTipificacionesHLA().get(donante.getTipificacionesHLA().size() - 1);
            }
        }

        // 2. BUSCAR EN FAMILIARES
        if (hlaData == null) {
            Optional<Familiar> familiarOpt = familiarRepository.findByDni(donanteId)
                    .or(() -> familiarRepository.findByNumeroMuestra(donanteId));

            if (familiarOpt.isPresent()) {
                Familiar familiar = familiarOpt.get();
                if (familiar.getPaciente().getTipificacionesHLA() != null && !familiar.getPaciente().getTipificacionesHLA().isEmpty()) {
                    hlaData = familiar.getPaciente().getTipificacionesHLA().get(0);
                    nombreDonante = familiar.getNombre() + " (Familiar)";
                }
            }
        }

        if (hlaData == null) {
            throw new RuntimeException("Donante (Paciente o Familiar) no encontrado o sin tipificación HLA registrada.");
        }

        List<String> hlaAntigens = new ArrayList<>();
        BiConsumer<String, String> addAntigen = (prefix, value) -> {
            if (value != null && !value.trim().isEmpty() && !value.equalsIgnoreCase("NULL")) {
                if (value.contains("*")) {
                    hlaAntigens.add(value.toUpperCase());
                } else {
                    hlaAntigens.add(prefix + value.toUpperCase());
                }
            }
        };

        addAntigen.accept("A", hlaData.getLocusA01());
        addAntigen.accept("A", hlaData.getLocusA02());
        addAntigen.accept("B", hlaData.getLocusB01());
        addAntigen.accept("B", hlaData.getLocusB02());
        addAntigen.accept("C", hlaData.getLocusC01());
        addAntigen.accept("C", hlaData.getLocusC02());
        addAntigen.accept("DR", hlaData.getLocusDR01());
        addAntigen.accept("DR", hlaData.getLocusDR02());
        addAntigen.accept("DQA", hlaData.getLocusDQA01());
        addAntigen.accept("DQA", hlaData.getLocusDQA02());
        addAntigen.accept("DQB", hlaData.getLocusDQB01());
        addAntigen.accept("DQB", hlaData.getLocusDQB02());
        addAntigen.accept("DPA", hlaData.getLocusDPA01());
        addAntigen.accept("DPA", hlaData.getLocusDPA02());
        addAntigen.accept("DPB", hlaData.getLocusDPB01());
        addAntigen.accept("DPB", hlaData.getLocusDPB02());

        return new HLADonanteDTO(
                hlaAntigens.stream().distinct().collect(Collectors.toList()),
                nombreDonante
        );
    }
}