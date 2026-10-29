package com.pedidos.web;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.pedidos.service.PedidoService;
import com.pedidos.web.dto.PedidoRequest;
import com.pedidos.web.dto.PedidoResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/pedidos")
public class PedidoController {

	private final PedidoService pedidoService;

	public PedidoController(PedidoService pedidoService) {
		this.pedidoService = pedidoService;
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public PedidoResponse comprar(
			@RequestHeader(value = "X-Token", required = false) String token,
			@Valid @RequestBody PedidoRequest request) {
		return pedidoService.comprar(token, request);
	}

	@GetMapping
	public List<PedidoResponse> listar(@RequestHeader(value = "X-Token", required = false) String token) {
		return pedidoService.listar(token);
	}

	@GetMapping("/{id}")
	public PedidoResponse obtenerPorId(
			@RequestHeader(value = "X-Token", required = false) String token,
			@PathVariable Long id) {
		return pedidoService.obtenerPorId(token, id);
	}

}
