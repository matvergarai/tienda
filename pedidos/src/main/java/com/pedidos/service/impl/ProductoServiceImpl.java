package com.pedidos.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pedidos.exception.RecursoNoEncontradoException;
import com.pedidos.mapper.ProductoMapper;
import com.pedidos.repository.ProductoRepository;
import com.pedidos.service.ProductoService;
import com.pedidos.service.UsuarioCliente;
import com.pedidos.web.dto.ProductoResponse;

@Service
public class ProductoServiceImpl implements ProductoService {

	private final ProductoRepository productoRepository;
	private final ProductoMapper productoMapper;
	private final UsuarioCliente usuarioCliente;

	public ProductoServiceImpl(
			ProductoRepository productoRepository,
			ProductoMapper productoMapper,
			UsuarioCliente usuarioCliente) {
		this.productoRepository = productoRepository;
		this.productoMapper = productoMapper;
		this.usuarioCliente = usuarioCliente;
	}

	@Override
	@Transactional(readOnly = true)
	public List<ProductoResponse> buscar(String token, String texto) {
		usuarioCliente.obtener(token);
		String filtro = texto == null ? null : texto.trim();
		return productoRepository.buscar(filtro).stream()
				.map(productoMapper::toResponse)
				.toList();
	}

	@Override
	@Transactional(readOnly = true)
	public ProductoResponse obtenerPorId(String token, Long id) {
		usuarioCliente.obtener(token);
		return productoRepository.findById(id)
				.map(productoMapper::toResponse)
				.orElseThrow(() -> new RecursoNoEncontradoException("Producto no encontrado con id " + id));
	}

}
