package com.tienda.service;

import java.util.List;

import com.tienda.web.dto.ProductoResponse;

public interface ProductoService {

	List<ProductoResponse> buscar(String token, String texto);

	ProductoResponse obtenerPorId(String token, Long id);

}
