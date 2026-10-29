package com.pedidos.service;

import java.util.List;

import com.pedidos.web.dto.PedidoRequest;
import com.pedidos.web.dto.PedidoResponse;

public interface PedidoService {

	PedidoResponse comprar(String token, PedidoRequest request);

	List<PedidoResponse> listar(String token);

	PedidoResponse obtenerPorId(String token, Long id);

}
