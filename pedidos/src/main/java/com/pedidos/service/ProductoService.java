package com.pedidos.service;

import java.util.List;

import com.pedidos.web.dto.ProductoResponse;

public interface ProductoService {

	List<ProductoResponse> buscar(String token, String texto);

	ProductoResponse obtenerPorId(String token, Long id);

}
