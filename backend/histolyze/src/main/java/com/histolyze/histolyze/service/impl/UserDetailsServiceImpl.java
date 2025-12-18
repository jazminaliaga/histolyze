package com.histolyze.histolyze.service.impl;

import com.histolyze.histolyze.model.Usuario;
import com.histolyze.histolyze.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String dni) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByDni(dni)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + dni));

        return org.springframework.security.core.userdetails.User
                .withUsername(usuario.getDni())
                .password(usuario.getPassword())
                .roles(usuario.getRole().name()) // importante: el enum se convierte a texto
                .build();
    }
}
