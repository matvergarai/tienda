package com.tienda.service;

import java.util.List;

import com.tienda.web.dto.PedidoRequest;
import com.tienda.web.dto.PedidoResponse;

public interface PedidoService {

	PedidoResponse comprar(String token, PedidoRequest request);

	List<PedidoResponse> listar(String token);

	PedidoResponse obtenerPorId(String token, Long id);

}
