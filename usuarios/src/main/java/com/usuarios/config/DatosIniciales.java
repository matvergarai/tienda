package com.usuarios.config;

import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.usuarios.model.Rol;
import com.usuarios.model.Usuario;
import com.usuarios.repository.UsuarioRepository;

@Configuration
public class DatosIniciales {

	@Bean
	ApplicationRunner administradorInicial(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
		return args -> {
			if (usuarioRepository.existsByCorreo("admin@tienda.cl")) {
				return;
			}
			Usuario admin = new Usuario();
			admin.setNombre("Administrador");
			admin.setCorreo("admin@tienda.cl");
			admin.setClave(passwordEncoder.encode("Admin12345"));
			admin.setRol(Rol.ADMINISTRADOR);
			usuarioRepository.save(admin);
		};
	}

}
