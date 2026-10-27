package com.tienda.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tienda.exception.RecursoNoEncontradoException;
import com.tienda.mapper.ProductoMapper;
import com.tienda.repository.ProductoRepository;
import com.tienda.service.ProductoService;
import com.tienda.service.SesionService;
import com.tienda.web.dto.ProductoResponse;

@Service
public class ProductoServiceImpl implements ProductoService {

	private final ProductoRepository productoRepository;
	private final ProductoMapper productoMapper;
	private final SesionService sesionService;

	public ProductoServiceImpl(
			ProductoRepository productoRepository,
			ProductoMapper productoMapper,
			SesionService sesionService) {
		this.productoRepository = productoRepository;
		this.productoMapper = productoMapper;
		this.sesionService = sesionService;
	}

	@Override
	@Transactional(readOnly = true)
	public List<ProductoResponse> buscar(String token, String texto) {
		sesionService.exigir(token);
		String filtro = texto == null ? null : texto.trim();
		return productoRepository.buscar(filtro).stream()
				.map(productoMapper::toResponse)
				.toList();
	}

	@Override
	@Transactional(readOnly = true)
	public ProductoResponse obtenerPorId(String token, Long id) {
		sesionService.exigir(token);
		return productoRepository.findById(id)
				.map(productoMapper::toResponse)
				.orElseThrow(() -> new RecursoNoEncontradoException("Producto no encontrado con id " + id));
	}

}
