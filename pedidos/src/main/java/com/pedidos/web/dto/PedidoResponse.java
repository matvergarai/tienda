package com.pedidos.web.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.pedidos.model.EstadoPago;

public record PedidoResponse(
		Long id,
		LocalDateTime fechaCreacion,
		Long clienteId,
		String clienteNombre,
		BigDecimal total,
		EstadoPago estadoPago,
		boolean pagoSimulado,
		String mensaje,
		List<ItemPedidoResponse> items) {
}
