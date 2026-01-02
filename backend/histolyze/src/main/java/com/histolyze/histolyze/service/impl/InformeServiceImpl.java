package com.histolyze.histolyze.service.impl;

import com.histolyze.histolyze.dto.*;
import com.histolyze.histolyze.model.*;
import com.histolyze.histolyze.repository.*;
import com.histolyze.histolyze.service.InformeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class InformeServiceImpl implements InformeService {

    @Autowired
    private PacienteRepository pacienteRepository;

    @Autowired
    private DSARepository dsaRepository;

    @Autowired
    private AnticuerpoAntiHLARepository anticuerpoRepository;

    @Autowired
    private TipificacionesHLARepository tipificacionesHLARepository;

    @Autowired
    private AntecedenteRepository antecedenteRepository;

    // @Autowired
    // private FamiliarRepository familiarRepository; // No se usa explícitamente aquí, pero puedes dejarlo si lo necesitas luego

    @Override
    public InformeDsaResponseDTO getDatosInformeDsa(String dni) {

        // 1. Buscar Paciente
        Paciente paciente = pacienteRepository.findByDni(dni)
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado con DNI: " + dni));

        // 2. Buscar último DSA
        DSA ultimoDsa = dsaRepository.findByPacienteOrderByFechaDesc(paciente)
                .stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No se encontraron estudios DSA para el paciente: " + dni));

        // 3. Traer anticuerpos
        List<AnticuerpoAntiHLA> anticuerpos = anticuerpoRepository.findByDsa(ultimoDsa);

        // 4. Armar el DTO de respuesta (USANDO EL NUEVO CONSTRUCTOR PLANO)
        // El orden de los parámetros debe coincidir con @AllArgsConstructor de InformeDsaResponseDTO:
        // (nombrePaciente, dni, muestra, fecha, observaciones, dsaSimple, anticuerpos)

        return new InformeDsaResponseDTO(
                ultimoDsa.getNombrePaciente(),
                ultimoDsa.getDniPaciente(),
                ultimoDsa.getNumeroMuestra(),
                ultimoDsa.getFecha(),
                ultimoDsa.getObservaciones(),
                null, // DsaSimpleDTO (lo dejamos null por ahora ya que usamos los campos planos)
                anticuerpos
        );
    }

    @Override
    public void guardarObservacionDsa(Long idDsa, String observaciones) {
        DSA dsa = dsaRepository.findById(idDsa)
                .orElseThrow(() -> new RuntimeException("Estudio DSA no encontrado con ID: " + idDsa));
        dsa.setObservaciones(observaciones);
        dsaRepository.save(dsa);
    }

    @Override
    public InformeHlaResponseDTO getDatosInformeHla(String dni) {
        Paciente paciente = pacienteRepository.findByDni(dni)
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado con DNI: " + dni));

        Optional<TipificacionesHLA> hlaOpt = tipificacionesHLARepository.findByPacienteOrderByFechaRegistroDesc(paciente)
                .stream().findFirst();

        Optional<Antecedente> antOpt = antecedenteRepository.findByPacienteOrderByIdAntecedenteDesc(paciente)
                .stream().findFirst();

        InformeHlaResponseDTO responseDto = new InformeHlaResponseDTO();
        responseDto.setNombre(paciente.getNombre() + " " + paciente.getApellido());
        responseDto.setDni(paciente.getDni());

        if (hlaOpt.isPresent()) {
            TipificacionesHLA hla = hlaOpt.get();
            responseDto.setMuestra(hla.getNumeroMuestra());
            responseDto.setHla(new HlaSimpleDTO(hla));
            responseDto.setIdHla(hla.getIdHla());
        }

        if (antOpt.isPresent()) {
            Antecedente ant = antOpt.get();
            if (ant.getGrupoSanguineo() != null) {
                responseDto.setGrupoSanguineo(ant.getGrupoSanguineo().name());
            }
        }

        return responseDto;
    }

    @Override
    public void guardarObservacionHla(Long idHla, String observaciones) {
        TipificacionesHLA hla = tipificacionesHLARepository.findById(idHla)
                .orElseThrow(() -> new RuntimeException("Registro HLA no encontrado con ID: " + idHla));
        hla.setObservaciones(observaciones);
        tipificacionesHLARepository.save(hla);
    }

    @Override
    public InformeFamiliarResponseDTO getDatosInformeFamiliar(String dni) {
        Paciente paciente = pacienteRepository.findByDni(dni)
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado con DNI: " + dni));

        TipificacionesHLA hlaReferencia = tipificacionesHLARepository.findByPacienteOrderByFechaRegistroDesc(paciente)
                .stream().findFirst()
                .orElse(null);

        // Crear DTO del paciente
        FamiliarReporteDTO pacienteDto = new FamiliarReporteDTO(hlaReferencia);

        // CORRECCIÓN IMPORTANTE: Asegurar que el DNI y Nombre estén seteados incluso si hlaReferencia es null
        if (paciente != null) {
            pacienteDto.setDni(paciente.getDni());
            if (pacienteDto.getNombre() == null) {
                pacienteDto.setNombre(paciente.getNombre() + " " + paciente.getApellido());
            }
        }

        List<Familiar> familiares = paciente.getFamiliares() != null ? paciente.getFamiliares() : Collections.emptyList();

        List<FamiliarReporteDTO> donantesDto = familiares.stream()
                .map(FamiliarReporteDTO::new)
                .collect(Collectors.toList());

        String notaGeneral = (hlaReferencia != null) ? hlaReferencia.getObservacionesFamiliar() : null;
        Long idHlaRef = (hlaReferencia != null) ? hlaReferencia.getIdHla() : null;

        return new InformeFamiliarResponseDTO(pacienteDto, donantesDto, notaGeneral, idHlaRef);
    }

    @Override
    public void guardarObservacionFamiliar(Long idHlaReferencia, String observaciones) {
        if (idHlaReferencia == null) {
            throw new RuntimeException("No se puede guardar la nota sin un estudio HLA de referencia.");
        }
        TipificacionesHLA hla = tipificacionesHLARepository.findById(idHlaReferencia)
                .orElseThrow(() -> new RuntimeException("Registro HLA de referencia no encontrado con ID: " + idHlaReferencia));
        hla.setObservacionesFamiliar(observaciones);
        tipificacionesHLARepository.save(hla);
    }
}