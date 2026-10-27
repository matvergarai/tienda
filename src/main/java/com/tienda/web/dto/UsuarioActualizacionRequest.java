package com.tienda.web.dto;

import com.tienda.model.Rol;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UsuarioActualizacionRequest(
		@NotBlank(message = "El nombre es obligatorio")
		@Size(max = 120, message = "El nombre no puede superar 120 caracteres")
		String nombre,

		@NotBlank(message = "El correo es obligatorio")
		@Email(message = "El correo no es valido")
		@Size(max = 150, message = "El correo no puede superar 150 caracteres")
		String correo,

		@Size(min = 6, max = 72, message = "La clave debe tener entre 6 y 72 caracteres")
		String clave,

		Rol rol) {
}
