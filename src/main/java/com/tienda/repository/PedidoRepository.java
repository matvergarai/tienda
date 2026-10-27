package com.tienda.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.tienda.model.Pedido;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {

	@EntityGraph(attributePaths = {"cliente", "detalles", "detalles.producto"})
	List<Pedido> findByCliente_IdOrderByFechaCreacionDesc(Long clienteId);

	@EntityGraph(attributePaths = {"cliente", "detalles", "detalles.producto"})
	List<Pedido> findAllByOrderByFechaCreacionDesc();

	@Override
	@EntityGraph(attributePaths = {"cliente", "detalles", "detalles.producto"})
	Optional<Pedido> findById(Long id);

}
