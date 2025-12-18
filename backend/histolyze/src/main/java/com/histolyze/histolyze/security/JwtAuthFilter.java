package com.histolyze.histolyze.security;

import io.jsonwebtoken.JwtException; // Puedes mantener esta importación o quitarla
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // --- CAMBIO 1: CONFIRMAR EJECUCIÓN ---
        System.out.println(">>> JwtAuthFilter ejecutado para la ruta: " + request.getRequestURI());

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userDni;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        jwt = authHeader.substring(7);

        try {
            userDni = jwtService.extractUsername(jwt);

            if (userDni != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(userDni);
                if (jwtService.isTokenValid(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    System.out.println(">>> Usuario '" + userDni + "' autenticado correctamente.");
                }
            }
        } catch (Exception e) { // --- CAMBIO 2: ATRAPAR CUALQUIER ERROR ---
            System.err.println("!!! Ocurrió un error inesperado en JwtAuthFilter !!!");
            System.err.println("Clase de la excepción: " + e.getClass().getName());
            System.err.println("Mensaje: " + e.getMessage());
            // Imprimimos el stack trace completo para tener todos los detalles
            e.printStackTrace();
        }

        filterChain.doFilter(request, response);
    }
}