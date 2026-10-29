package com.pedidos.config;

import java.math.BigDecimal;

import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.pedidos.model.Producto;
import com.pedidos.repository.ProductoRepository;

@Configuration
public class DatosIniciales {

	@Bean
	ApplicationRunner productosIniciales(ProductoRepository productoRepository) {
		return args -> {
			if (productoRepository.count() > 0) {
				return;
			}
			guardar(productoRepository, "Audifonos inalambricos", "Bluetooth con cancelacion de ruido", "24990", 20);
			guardar(productoRepository, "Teclado mecanico", "Switches tactiles y retroiluminacion", "39990", 15);
			guardar(productoRepository, "Mouse ergonomico", "Diseno vertical para uso prolongado", "15990", 25);
			guardar(productoRepository, "Monitor 24 pulgadas", "Panel Full HD para estudio y oficina", "129990", 10);
			guardar(productoRepository, "Webcam HD", "Camara 1080p con microfono integrado", "29990", 18);
		};
	}

	private void guardar(ProductoRepository productoRepository, String nombre, String descripcion, String precio, int stock) {
		Producto producto = new Producto();
		producto.setNombre(nombre);
		producto.setDescripcion(descripcion);
		producto.setPrecio(new BigDecimal(precio));
		producto.setStock(stock);
		productoRepository.save(producto);
	}

}
