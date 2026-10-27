package com.tienda.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tienda.model.Rol;
import com.tienda.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

	Optional<Usuario> findByCorreo(String correo);

	boolean existsByCorreo(String correo);

	boolean existsByCorreoAndIdNot(String correo, Long id);

	long countByRol(Rol rol);

}
