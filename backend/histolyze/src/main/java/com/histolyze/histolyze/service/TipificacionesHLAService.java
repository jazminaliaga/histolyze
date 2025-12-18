package com.histolyze.histolyze.service;

import com.histolyze.histolyze.model.TipificacionesHLA;
import java.util.List;
import java.util.Optional;

public interface TipificacionesHLAService {
    List<TipificacionesHLA> listarTipificaciones();

    Optional<TipificacionesHLA> obtenerPorId(Long id);
    TipificacionesHLA guardarTipificacion(TipificacionesHLA tipificacion, Long idPaciente);
    TipificacionesHLA actualizarTipificacion(Long idHla, TipificacionesHLA tipificacion, Long idPaciente);
    void eliminarTipificacion(Long id);
    List<TipificacionesHLA> findByPaciente(Long idPaciente);
}