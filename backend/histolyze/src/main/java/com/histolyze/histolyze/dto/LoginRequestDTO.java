package com.histolyze.histolyze.dto;

// Usamos un 'record' de Java. Es una forma moderna y súper concisa
// de crear una clase que solo transporta datos.
// Automáticamente crea los campos, el constructor, getters, etc.
public record LoginRequestDTO(String dni, String password) {
}