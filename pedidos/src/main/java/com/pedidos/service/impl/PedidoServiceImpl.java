package com.pedidos.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pedidos.exception.AccesoDenegadoException;
import com.pedidos.exception.ConflictoException;
import com.pedidos.exception.RecursoNoEncontradoException;
import com.pedidos.mapper.PedidoMapper;
import com.pedidos.model.DetallePedido;
import com.pedidos.model.EstadoPago;
import com.pedidos.model.Pedido;
import com.pedidos.model.Producto;
import com.pedidos.model.Rol;
import com.pedidos.repository.PedidoRepository;
import com.pedidos.repository.ProductoRepository;
import com.pedidos.service.PedidoService;
import com.pedidos.service.UsuarioCliente;
import com.pedidos.service.UsuarioSesion;
import com.pedidos.web.dto.ItemPedidoRequest;
import com.pedidos.web.dto.PedidoRequest;
import com.pedidos.web.dto.PedidoResponse;

@Service
public class PedidoServiceImpl implements PedidoService {

	static final String MENSAJE_PAGO = "Pago simulado realizado con exito";

	private final PedidoRepository pedidoRepository;
	private final ProductoRepository productoRepository;
	private final PedidoMapper pedidoMapper;
	private final UsuarioCliente usuarioCliente;

	public PedidoServiceImpl(
			PedidoRepository pedidoRepository,
			ProductoRepository productoRepository,
			PedidoMapper pedidoMapper,
			UsuarioCliente usuarioCliente) {
		this.pedidoRepository = pedidoRepository;
		this.productoRepository = productoRepository;
		this.pedidoMapper = pedidoMapper;
		this.usuarioCliente = usuarioCliente;
	}

	@Override
	@Transactional
	public PedidoResponse comprar(String token, PedidoRequest request) {
		UsuarioSesion cliente = exigirRol(token, Rol.CLIENTE);
		Pedido pedido = new Pedido();
		pedido.setClienteId(cliente.id());
		pedido.setClienteNombre(cliente.nombre());
		pedido.setFechaCreacion(LocalDateTime.now());
		pedido.setEstadoPago(EstadoPago.PAGADO);
		pedido.setPagoSimulado(true);
		pedido.setMensaje(MENSAJE_PAGO);

		List<DetallePedido> detalles = new ArrayList<>();
		BigDecimal total = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
		for (ItemPedidoRequest item : request.items()) {
			Producto producto = productoRepository.findById(item.productoId())
					.orElseThrow(() -> new RecursoNoEncontradoException(
							"Producto no encontrado con id " + item.productoId()));
			if (producto.getStock() < item.cantidad()) {
				throw new ConflictoException("No hay stock suficiente de " + producto.getNombre());
			}
			producto.setStock(producto.getStock() - item.cantidad());
			BigDecimal subtotal = producto.getPrecio()
					.multiply(BigDecimal.valueOf(item.cantidad()))
					.setScale(2, RoundingMode.HALF_UP);
			DetallePedido detalle = new DetallePedido();
			detalle.setPedido(pedido);
			detalle.setProducto(producto);
			detalle.setCantidad(item.cantidad());
			detalle.setPrecioUnitario(producto.getPrecio());
			detalle.setSubtotal(subtotal);
			detalles.add(detalle);
			total = total.add(subtotal);
		}
		pedido.setTotal(total);
		pedido.setDetalles(detalles);
		return pedidoMapper.toResponse(pedidoRepository.save(pedido));
	}

	@Override
	@Transactional(readOnly = true)
	public List<PedidoResponse> listar(String token) {
		UsuarioSesion actor = usuarioCliente.obtener(token);
		List<Pedido> pedidos = actor.rol() == Rol.ADMINISTRADOR
				? pedidoRepository.findAllByOrderByFechaCreacionDesc()
				: pedidoRepository.findByClienteIdOrderByFechaCreacionDesc(actor.id());
		return pedidos.stream().map(pedidoMapper::toResponse).toList();
	}

	@Override
	@Transactional(readOnly = true)
	public PedidoResponse obtenerPorId(String token, Long id) {
		UsuarioSesion actor = usuarioCliente.obtener(token);
		Pedido pedido = pedidoRepository.findById(id)
				.orElseThrow(() -> new RecursoNoEncontradoException("Pedido no encontrado con id " + id));
		if (actor.rol() != Rol.ADMINISTRADOR && !pedido.getClienteId().equals(actor.id())) {
			throw new AccesoDenegadoException();
		}
		return pedidoMapper.toResponse(pedido);
	}

	private UsuarioSesion exigirRol(String token, Rol rol) {
		UsuarioSesion sesion = usuarioCliente.obtener(token);
		if (sesion.rol() != rol) {
			throw new AccesoDenegadoException();
		}
		return sesion;
	}

}
