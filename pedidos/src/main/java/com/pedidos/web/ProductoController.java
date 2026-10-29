package com.pedidos.web;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pedidos.service.ProductoService;
import com.pedidos.web.dto.ProductoResponse;

@RestController
@RequestMapping("/productos")
public class ProductoController {

	private final ProductoService productoService;

	public ProductoController(ProductoService productoService) {
		this.productoService = productoService;
	}

	@GetMapping
	public List<ProductoResponse> buscar(
			@RequestHeader(value = "X-Token", required = false) String token,
			@RequestParam(required = false) String texto) {
		return productoService.buscar(token, texto);
	}

	@GetMapping("/{id}")
	public ProductoResponse obtenerPorId(
			@RequestHeader(value = "X-Token", required = false) String token,
			@PathVariable Long id) {
		return productoService.obtenerPorId(token, id);
	}

}
