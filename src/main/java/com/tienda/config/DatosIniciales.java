package com.tienda.config;

import java.math.BigDecimal;

import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.tienda.model.Producto;
import com.tienda.model.Rol;
import com.tienda.model.Usuario;
import com.tienda.repository.ProductoRepository;
import com.tienda.repository.UsuarioRepository;

@Configuration
public class DatosIniciales {

	@Bean
	ApplicationRunner datosDeLaTienda(
			UsuarioRepository usuarioRepository,
			ProductoRepository productoRepository,
			PasswordEncoder passwordEncoder) {
		return args -> {
			if (!usuarioRepository.existsByCorreo("admin@tienda.cl")) {
				Usuario admin = new Usuario();
				admin.setNombre("Administrador");
				admin.setCorreo("admin@tienda.cl");
				admin.setClave(passwordEncoder.encode("Admin12345"));
				admin.setRol(Rol.ADMINISTRADOR);
				usuarioRepository.save(admin);
			}
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
