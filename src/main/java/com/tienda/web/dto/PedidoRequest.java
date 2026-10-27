package com.tienda.web.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

public record PedidoRequest(
		@NotEmpty(message = "Debe incluir al menos un producto")
		List<@Valid ItemPedidoRequest> items) {
}
