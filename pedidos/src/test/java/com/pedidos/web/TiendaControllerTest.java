package com.pedidos.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import com.pedidos.exception.AccesoDenegadoException;
import com.pedidos.exception.CredencialesInvalidasException;
import com.pedidos.model.EstadoPago;
import com.pedidos.service.PedidoService;
import com.pedidos.service.ProductoService;
import com.pedidos.web.dto.ItemPedidoResponse;
import com.pedidos.web.dto.PedidoResponse;
import com.pedidos.web.dto.ProductoResponse;

@ExtendWith(MockitoExtension.class)
class TiendaControllerTest {

	@Mock
	private ProductoService productoService;

	@Mock
	private PedidoService pedidoService;

	private MockMvc mockMvc;

	@BeforeEach
	void setUp() {
		LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
		validator.afterPropertiesSet();
		mockMvc = MockMvcBuilders.standaloneSetup(
				new ProductoController(productoService),
				new PedidoController(pedidoService))
				.setControllerAdvice(new ManejadorExcepciones())
				.setValidator(validator)
				.build();
	}

	@Test
	void buscaProductosConSesion() throws Exception {
		when(productoService.buscar(eq("token-cliente"), eq("monitor")))
				.thenReturn(List.of(new ProductoResponse(4L, "Monitor 24 pulgadas", "Full HD", new BigDecimal("129990"), 10)));

		mockMvc.perform(get("/productos").param("texto", "monitor").header("X-Token", "token-cliente"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].nombre").value("Monitor 24 pulgadas"));
	}

	@Test
	void responde401SiBuscaSinSesion() throws Exception {
		when(productoService.buscar(eq(null), eq(null)))
				.thenThrow(new CredencialesInvalidasException("Debe iniciar sesion"));

		mockMvc.perform(get("/productos"))
				.andExpect(status().isUnauthorized());
	}

	@Test
	void registraUnaCompra() throws Exception {
		when(pedidoService.comprar(eq("token-cliente"), any())).thenReturn(pedido());

		mockMvc.perform(post("/pedidos")
				.header("X-Token", "token-cliente")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{"items":[{"productoId":1,"cantidad":2}]}
						"""))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.pagoSimulado").value(true))
				.andExpect(jsonPath("$.estadoPago").value("PAGADO"));
	}

	@Test
	void responde403SiUnAdministradorCompra() throws Exception {
		when(pedidoService.comprar(eq("token-admin"), any())).thenThrow(new AccesoDenegadoException());

		mockMvc.perform(post("/pedidos")
				.header("X-Token", "token-admin")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{"items":[{"productoId":1,"cantidad":1}]}
						"""))
				.andExpect(status().isForbidden());
	}

	@Test
	void responde400SiLaCompraNoTraeProductos() throws Exception {
		mockMvc.perform(post("/pedidos")
				.header("X-Token", "token-cliente")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{"items":[]}
						"""))
				.andExpect(status().isBadRequest());
	}

	private PedidoResponse pedido() {
		return new PedidoResponse(
				1L,
				LocalDateTime.of(2026, 10, 27, 18, 0),
				2L,
				"Ana Perez",
				new BigDecimal("49980.00"),
				EstadoPago.PAGADO,
				true,
				"Pago simulado realizado con exito",
				List.of(new ItemPedidoResponse(1L, "Audifonos inalambricos", 2, new BigDecimal("24990"), new BigDecimal("49980.00"))));
	}

}
