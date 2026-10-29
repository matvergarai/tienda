package com.pedidos.mapper;

import org.springframework.stereotype.Component;

import com.pedidos.model.Producto;
import com.pedidos.web.dto.ProductoResponse;

@Component
public class ProductoMapper {

	public ProductoResponse toResponse(Producto producto) {
		return new ProductoResponse(
				producto.getId(),
				producto.getNombre(),
				producto.getDescripcion(),
				producto.getPrecio(),
				producto.getStock());
	}

}
