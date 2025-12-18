package com.histolyze.histolyze.config;

import com.histolyze.histolyze.model.Usuario;
import com.histolyze.histolyze.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // DNI del usuario administrador que queremos crear
        String adminDni = "00000001";

        // 1. Verificamos si el usuario ya existe en la base de datos
        if (!usuarioRepository.findByDni(adminDni).isPresent()) {

            System.out.println("Creando usuario administrador por primera vez...");

            // 2. Si no existe, creamos un nuevo objeto Usuario
            Usuario admin = new Usuario();
            admin.setNombre("Administrador");
            admin.setApellido("1");
            admin.setDni(adminDni);

            // 3. Encriptamos la contraseña "admin123" usando el mismo encriptador que Spring Security
            admin.setPassword(passwordEncoder.encode("admin123"));

            admin.setRole(Usuario.Rol.ADMIN);

            // 4. Guardamos el nuevo usuario en la base de datos
            usuarioRepository.save(admin);

            System.out.println("Usuario administrador creado con éxito. DNI: " + adminDni + ", Contraseña: admin123");
        }
    }
}