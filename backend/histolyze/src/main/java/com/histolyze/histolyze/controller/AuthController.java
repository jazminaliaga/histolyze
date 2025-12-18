package com.histolyze.histolyze.controller;

import com.histolyze.histolyze.dto.AuthResponseDTO;
import com.histolyze.histolyze.dto.LoginRequestDTO;
import com.histolyze.histolyze.model.Usuario;
import com.histolyze.histolyze.repository.UsuarioRepository;
import com.histolyze.histolyze.security.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody LoginRequestDTO loginRequest) {
        // 1. Spring Security valida el DNI y la contraseña
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.dni(), loginRequest.password())
        );

        // 2. Si la autenticación es exitosa, procedemos
        if (authentication.isAuthenticated()) {
            // Obtenemos el UserDetails directamente del objeto de autenticación
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            // 3. Generamos el token usando el UserDetails que Spring nos dio
            String token = jwtService.generateToken(userDetails);

            // 4. Buscamos el objeto Usuario completo para enviarlo al frontend
            Usuario usuario = usuarioRepository.findByDni(loginRequest.dni())
                    .orElseThrow(() -> new UsernameNotFoundException("Error al buscar usuario post-login"));

            // 5. Devolvemos el token y los datos del usuario
            return ResponseEntity.ok(new AuthResponseDTO(token, usuario));
        } else {
            throw new UsernameNotFoundException("DNI o contraseña incorrectos");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<Usuario> register(@RequestBody Usuario usuario) {
        // Encriptamos la contraseña antes de guardarla
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        Usuario savedUsuario = usuarioRepository.save(usuario);
        return ResponseEntity.ok(savedUsuario);
    }
}