package com.pedidos.web.dto;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(
		int status,
		String error,
		String mensaje,
		Map<String, String> campos) {
}
