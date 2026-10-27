package com.tienda.web.dto;

import com.tienda.model.Rol;

public record LoginResponse(
		Long id,
		String nombre,
		String correo,
		Rol rol,
		String token) {
}
