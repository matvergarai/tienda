package com.usuarios.web.dto;

import com.usuarios.model.Rol;

public record LoginResponse(
		Long id,
		String nombre,
		String correo,
		Rol rol,
		String token) {
}
