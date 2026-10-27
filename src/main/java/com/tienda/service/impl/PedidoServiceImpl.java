package com.tienda.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tienda.exception.AccesoDenegadoException;
import com.tienda.exception.ConflictoException;
import com.tienda.exception.RecursoNoEncontradoException;
import com.tienda.mapper.PedidoMapper;
import com.tienda.model.DetallePedido;
import com.tienda.model.EstadoPago;
import com.tienda.model.Pedido;
import com.tienda.model.Producto;
import com.tienda.model.Rol;
import com.tienda.model.Usuario;
import com.tienda.repository.PedidoRepository;
import com.tienda.repository.ProductoRepository;
import com.tienda.service.PedidoService;
import com.tienda.service.SesionService;
import com.tienda.web.dto.ItemPedidoRequest;
import com.tienda.web.dto.PedidoRequest;
import com.tienda.web.dto.PedidoResponse;

@Service
public class PedidoServiceImpl implements PedidoService {

	static final String MENSAJE_PAGO = "Pago simulado realizado con exito";

	private final PedidoRepository pedidoRepository;
	private final ProductoRepository productoRepository;
	private final PedidoMapper pedidoMapper;
	private final SesionService sesionService;

	public PedidoServiceImpl(
			PedidoRepository pedidoRepository,
			ProductoRepository productoRepository,
			PedidoMapper pedidoMapper,
			SesionService sesionService) {
		this.pedidoRepository = pedidoRepository;
		this.productoRepository = productoRepository;
		this.pedidoMapper = pedidoMapper;
		this.sesionService = sesionService;
	}

	@Override
	@Transactional
	public PedidoResponse comprar(String token, PedidoRequest request) {
		Usuario cliente = sesionService.exigirRol(token, Rol.CLIENTE);
		Pedido pedido = new Pedido();
		pedido.setCliente(cliente);
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
		Usuario actor = sesionService.exigir(token);
		List<Pedido> pedidos = actor.getRol() == Rol.ADMINISTRADOR
				? pedidoRepository.findAllByOrderByFechaCreacionDesc()
				: pedidoRepository.findByCliente_IdOrderByFechaCreacionDesc(actor.getId());
		return pedidos.stream().map(pedidoMapper::toResponse).toList();
	}

	@Override
	@Transactional(readOnly = true)
	public PedidoResponse obtenerPorId(String token, Long id) {
		Usuario actor = sesionService.exigir(token);
		Pedido pedido = pedidoRepository.findById(id)
				.orElseThrow(() -> new RecursoNoEncontradoException("Pedido no encontrado con id " + id));
		if (actor.getRol() != Rol.ADMINISTRADOR && !pedido.getCliente().getId().equals(actor.getId())) {
			throw new AccesoDenegadoException();
		}
		return pedidoMapper.toResponse(pedido);
	}

}
