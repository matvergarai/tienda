package com.tienda.mapper;

import org.springframework.stereotype.Component;

import com.tienda.model.Usuario;
import com.tienda.web.dto.LoginResponse;
import com.tienda.web.dto.UsuarioResponse;

@Component
public class UsuarioMapper {

	public UsuarioResponse toResponse(Usuario usuario) {
		return new UsuarioResponse(usuario.getId(), usuario.getNombre(), usuario.getCorreo(), usuario.getRol());
	}

	public LoginResponse toLogin(Usuario usuario, String token) {
		return new LoginResponse(usuario.getId(), usuario.getNombre(), usuario.getCorreo(), usuario.getRol(), token);
	}

}
