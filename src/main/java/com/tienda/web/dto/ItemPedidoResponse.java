package com.tienda.web.dto;

import java.math.BigDecimal;

public record ItemPedidoResponse(
		Long productoId,
		String productoNombre,
		Integer cantidad,
		BigDecimal precioUnitario,
		BigDecimal subtotal) {
}
