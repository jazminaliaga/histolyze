package com.histolyze.histolyze.config;

import com.histolyze.histolyze.security.JwtAuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.cors.CorsConfigurationSource;
import java.util.Arrays;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider; // Inyectamos el proveedor de ApplicationConfig

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Rutas Públicas: solo para login y registro
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/api/auth/**").permitAll()

                        // Permitimos nuestro endpoint de prueba para subir archivos
                        .requestMatchers("/api/reports/**").permitAll()

                        // Rutas de Administración: solo para usuarios con rol ADMIN
                        .requestMatchers("/api/usuarios/**").hasAuthority("ROLE_ADMIN")

                        .requestMatchers(HttpMethod.PUT, "/api/pacientes/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/api/dsa/**").authenticated()

                        // Rutas de Pacientes y Datos Clínicos: para cualquier usuario autenticado
                        .requestMatchers(
                                "/api/pacientes/**",
                                "/api/antecedentes/**",
                                "/api/dsa/**",
                                "/api/anticuerpos/**",
                                "/api/crossmatch/**"
                        ).authenticated()

                        // Cualquier otra ruta no definida explícitamente requiere autenticación
                        .anyRequest().authenticated()
                )
                // ¡CAMBIO CLAVE! Conectamos el proveedor de autenticación a la cadena de seguridad.
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://127.0.0.1:5500"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}