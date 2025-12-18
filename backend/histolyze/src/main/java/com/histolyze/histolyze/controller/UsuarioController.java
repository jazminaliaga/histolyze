package com.histolyze.histolyze.controller;

import com.histolyze.histolyze.model.Usuario;
import com.histolyze.histolyze.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Usuario crearUsuario(@RequestBody Usuario usuario) {
        return usuarioService.crearUsuario(usuario);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<Usuario> listarUsuarios() {
        return usuarioService.listarUsuarios();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Usuario> actualizarUsuario(@PathVariable Long id, @RequestBody Usuario datosUsuario) {
        // 1. Llama al método del servicio que creamos en el paso anterior.
        Usuario usuarioActualizado = usuarioService.actualizarUsuario(id, datosUsuario);
        // 2. Devuelve una respuesta HTTP 200 OK con el usuario actualizado en el cuerpo.
        return ResponseEntity.ok(usuarioActualizado);
    }

    @PutMapping("/{id}/cambiar-clave")
    public Usuario cambiarClave(@PathVariable Long id, @RequestBody String nuevaClave) {
        return usuarioService.cambiarClave(id, nuevaClave);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> eliminarUsuario(@PathVariable Long id) {
        usuarioService.eliminarUsuario(id);
        // Devuelve una respuesta HTTP 204 No Content, que es el estándar
        // para una eliminación exitosa.
        return ResponseEntity.noContent().build();
    }
}
