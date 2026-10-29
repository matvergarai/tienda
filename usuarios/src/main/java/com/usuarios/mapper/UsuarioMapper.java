package com.usuarios.mapper;

import org.springframework.stereotype.Component;

import com.usuarios.model.Usuario;
import com.usuarios.web.dto.LoginResponse;
import com.usuarios.web.dto.UsuarioResponse;

@Component
public class UsuarioMapper {

	public UsuarioResponse toResponse(Usuario usuario) {
		return new UsuarioResponse(usuario.getId(), usuario.getNombre(), usuario.getCorreo(), usuario.getRol());
	}

	public LoginResponse toLogin(Usuario usuario, String token) {
		return new LoginResponse(usuario.getId(), usuario.getNombre(), usuario.getCorreo(), usuario.getRol(), token);
	}

}
