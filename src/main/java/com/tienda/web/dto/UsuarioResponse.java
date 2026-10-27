package com.tienda.web.dto;

import com.tienda.model.Rol;

public record UsuarioResponse(
		Long id,
		String nombre,
		String correo,
		Rol rol) {
}
