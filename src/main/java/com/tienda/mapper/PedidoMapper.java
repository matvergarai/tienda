package com.tienda.mapper;

import org.springframework.stereotype.Component;

import com.tienda.model.DetallePedido;
import com.tienda.model.Pedido;
import com.tienda.web.dto.ItemPedidoResponse;
import com.tienda.web.dto.PedidoResponse;

@Component
public class PedidoMapper {

	public PedidoResponse toResponse(Pedido pedido) {
		return new PedidoResponse(
				pedido.getId(),
				pedido.getFechaCreacion(),
				pedido.getCliente().getId(),
				pedido.getCliente().getNombre(),
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
