package com.histolyze.histolyze.dto;

import com.histolyze.histolyze.model.Usuario;

public record AuthResponseDTO(String token, Usuario usuario) {
}