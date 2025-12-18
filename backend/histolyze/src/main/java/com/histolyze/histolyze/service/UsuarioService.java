package com.histolyze.histolyze.service;

import com.histolyze.histolyze.model.Usuario;
import java.util.List;

public interface UsuarioService {
    Usuario crearUsuario(Usuario usuario);
    Usuario cambiarClave(Long idUsuario, String nuevaClave);
    List<Usuario> listarUsuarios();
    Usuario buscarPorDni(String dni);
    Usuario actualizarUsuario(Long id, Usuario datosActualizados);
    void eliminarUsuario(Long id);
}
