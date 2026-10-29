package com.pedidos.mapper;

import org.springframework.stereotype.Component;

import com.pedidos.model.DetallePedido;
import com.pedidos.model.Pedido;
import com.pedidos.web.dto.ItemPedidoResponse;
import com.pedidos.web.dto.PedidoResponse;

@Component
public class PedidoMapper {

	public PedidoResponse toResponse(Pedido pedido) {
		return new PedidoResponse(
				pedido.getId(),
				pedido.getFechaCreacion(),
				pedido.getClienteId(),
				pedido.getClienteNombre(),
				pedido.getTotal(),
				pedido.getEstadoPago(),
				pedido.isPagoSimulado(),
				pedido.getMensaje(),
				pedido.getDetalles().stream().map(this::toItem).toList());
	}

	private ItemPedidoResponse toItem(DetallePedido detalle) {
		return new ItemPedidoResponse(
				detalle.getProducto().getId(),
				detalle.getProducto().getNombre(),
				detalle.getCantidad(),
				detalle.getPrecioUnitario(),
				detalle.getSubtotal());
	}

}
