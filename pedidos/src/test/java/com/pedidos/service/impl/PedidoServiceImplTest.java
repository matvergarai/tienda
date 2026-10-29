package com.pedidos.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.pedidos.exception.AccesoDenegadoException;
import com.pedidos.exception.ConflictoException;
import com.pedidos.mapper.PedidoMapper;
import com.pedidos.model.Pedido;
import com.pedidos.model.Producto;
import com.pedidos.model.Rol;
import com.pedidos.repository.PedidoRepository;
import com.pedidos.repository.ProductoRepository;
import com.pedidos.service.UsuarioCliente;
import com.pedidos.service.UsuarioSesion;
import com.pedidos.web.dto.ItemPedidoRequest;
import com.pedidos.web.dto.PedidoRequest;
import com.pedidos.web.dto.PedidoResponse;

@ExtendWith(MockitoExtension.class)
class PedidoServiceImplTest {

	@Mock
	private PedidoRepository pedidoRepository;

	@Mock
	private ProductoRepository productoRepository;

	@Mock
	private UsuarioCliente usuarioCliente;

	private PedidoServiceImpl service;

	@BeforeEach
	void setUp() {
		service = new PedidoServiceImpl(pedidoRepository, productoRepository, new PedidoMapper(), usuarioCliente);
	}

	@Test
	void unClienteCompraDescuentaStockYSimulaElPago() {
		Producto producto = producto();
		when(usuarioCliente.obtener("token-cliente")).thenReturn(sesion(2L, Rol.CLIENTE));
		when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
		when(pedidoRepository.save(any())).thenAnswer(invocacion -> {
			Pedido pedido = invocacion.getArgument(0);
			pedido.setId(8L);
			pedido.getDetalles().get(0).setId(1L);
			return pedido;
		});

		PedidoResponse respuesta = service.comprar("token-cliente", new PedidoRequest(List.of(new ItemPedidoRequest(1L, 2))));

		assertEquals(new BigDecimal("49980.00"), respuesta.total());
		assertEquals("PAGADO", respuesta.estadoPago().name());
		assertTrue(respuesta.pagoSimulado());
		assertEquals(PedidoServiceImpl.MENSAJE_PAGO, respuesta.mensaje());
		assertEquals(2L, respuesta.clienteId());
		assertEquals(18, producto.getStock());
	}

	@Test
	void rechazaLaCompraSiNoAlcanzaElStock() {
		Producto producto = producto();
		producto.setStock(1);
		when(usuarioCliente.obtener("token-cliente")).thenReturn(sesion(2L, Rol.CLIENTE));
		when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));

		assertThrows(ConflictoException.class,
				() -> service.comprar("token-cliente", new PedidoRequest(List.of(new ItemPedidoRequest(1L, 2)))));
		assertEquals(1, producto.getStock());
	}

	@Test
	void unAdministradorNoPuedeComprar() {
		when(usuarioCliente.obtener("token-admin")).thenReturn(sesion(1L, Rol.ADMINISTRADOR));

		assertThrows(AccesoDenegadoException.class,
				() -> service.comprar("token-admin", new PedidoRequest(List.of(new ItemPedidoRequest(1L, 1)))));
	}

	@Test
	void unClienteNoVeElPedidoDeOtro() {
		Pedido pedido = new Pedido();
		pedido.setId(4L);
		pedido.setClienteId(3L);
		when(usuarioCliente.obtener("token-cliente")).thenReturn(sesion(2L, Rol.CLIENTE));
		when(pedidoRepository.findById(4L)).thenReturn(Optional.of(pedido));

		assertThrows(AccesoDenegadoException.class, () -> service.obtenerPorId("token-cliente", 4L));
	}

	private UsuarioSesion sesion(Long id, Rol rol) {
		return new UsuarioSesion(id, "Persona", "ana@tienda.cl", rol);
	}

	private Producto producto() {
		Producto producto = new Producto();
		producto.setId(1L);
		producto.setNombre("Audifonos inalambricos");
		producto.setDescripcion("Bluetooth");
		producto.setPrecio(new BigDecimal("24990"));
		producto.setStock(20);
		return producto;
	}

}
