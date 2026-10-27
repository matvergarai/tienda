package com.tienda.mapper;

import org.springframework.stereotype.Component;

import com.tienda.model.Producto;
import com.tienda.web.dto.ProductoResponse;

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
