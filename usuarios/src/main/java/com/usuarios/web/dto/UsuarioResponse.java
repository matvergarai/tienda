package com.usuarios.web.dto;

import com.usuarios.model.Rol;

public record UsuarioResponse(
		Long id,
		String nombre,
		String correo,
		Rol rol) {
}
