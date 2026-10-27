package com.tienda.service.impl;

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

import com.tienda.exception.AccesoDenegadoException;
import com.tienda.exception.ConflictoException;
import com.tienda.mapper.PedidoMapper;
import com.tienda.model.Pedido;
import com.tienda.model.Producto;
import com.tienda.model.Rol;
import com.tienda.model.Usuario;
import com.tienda.repository.PedidoRepository;
import com.tienda.repository.ProductoRepository;
import com.tienda.repository.UsuarioRepository;
import com.tienda.service.SesionService;
import com.tienda.web.dto.ItemPedidoRequest;
import com.tienda.web.dto.PedidoRequest;
import com.tienda.web.dto.PedidoResponse;

@ExtendWith(MockitoExtension.class)
class PedidoServiceImplTest {

	@Mock
	private PedidoRepository pedidoRepository;

	@Mock
	private ProductoRepository productoRepository;

	@Mock
	private UsuarioRepository usuarioRepository;

	private SesionService sesionService;
	private PedidoServiceImpl service;

	@BeforeEach
	void setUp() {
		sesionService = new SesionService(usuarioRepository);
		service = new PedidoServiceImpl(pedidoRepository, productoRepository, new PedidoMapper(), sesionService);
	}

	@Test
	void unClienteCompraDescuentaStockYSimulaElPago() {
		Usuario cliente = usuario(2L, Rol.CLIENTE);
		Producto producto = producto();
		when(usuarioRepository.findById(2L)).thenReturn(Optional.of(cliente));
		when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
		when(pedidoRepository.save(any())).thenAnswer(invocacion -> {
			Pedido pedido = invocacion.getArgument(0);
			pedido.setId(8L);
			pedido.getDetalles().get(0).setId(1L);
			return pedido;
		});

		PedidoResponse respuesta = service.comprar(
				sesionService.abrir(cliente),
				new PedidoRequest(List.of(new ItemPedidoRequest(1L, 2))));

		assertEquals(new BigDecimal("49980.00"), respuesta.total());
		assertEquals("PAGADO", respuesta.estadoPago().name());
		assertTrue(respuesta.pagoSimulado());
		assertEquals(PedidoServiceImpl.MENSAJE_PAGO, respuesta.mensaje());
		assertEquals(18, producto.getStock());
	}

	@Test
	void rechazaLaCompraSiNoAlcanzaElStock() {
		Usuario cliente = usuario(2L, Rol.CLIENTE);
		Producto producto = producto();
		producto.setStock(1);
		when(usuarioRepository.findById(2L)).thenReturn(Optional.of(cliente));
		when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));

		assertThrows(ConflictoException.class, () -> service.comprar(
				sesionService.abrir(cliente),
				new PedidoRequest(List.of(new ItemPedidoRequest(1L, 2)))));
		assertEquals(1, producto.getStock());
	}

	@Test
	void unAdministradorNoPuedeComprar() {
		Usuario admin = usuario(1L, Rol.ADMINISTRADOR);
		when(usuarioRepository.findById(1L)).thenReturn(Optional.of(admin));

		assertThrows(AccesoDenegadoException.class, () -> service.comprar(
				sesionService.abrir(admin),
				new PedidoRequest(List.of(new ItemPedidoRequest(1L, 1)))));
	}

	@Test
	void unClienteNoVeElPedidoDeOtro() {
		Usuario cliente = usuario(2L, Rol.CLIENTE);
		Usuario otro = usuario(3L, Rol.CLIENTE);
		otro.setCorreo("otro@tienda.cl");
		Pedido pedido = new Pedido();
		pedido.setId(4L);
		pedido.setCliente(otro);
		when(usuarioRepository.findById(2L)).thenReturn(Optional.of(cliente));
		when(pedidoRepository.findById(4L)).thenReturn(Optional.of(pedido));

		assertThrows(AccesoDenegadoException.class,
				() -> service.obtenerPorId(sesionService.abrir(cliente), 4L));
	}

	private Usuario usuario(Long id, Rol rol) {
		Usuario usuario = new Usuario();
		usuario.setId(id);
		usuario.setNombre("Persona");
		usuario.setCorreo("ana@tienda.cl");
		usuario.setRol(rol);
		return usuario;
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
