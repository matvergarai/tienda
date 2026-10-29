package com.pedidos.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.pedidos.model.Pedido;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {

	@EntityGraph(attributePaths = {"detalles", "detalles.producto"})
	List<Pedido> findByClienteIdOrderByFechaCreacionDesc(Long clienteId);

	@EntityGraph(attributePaths = {"detalles", "detalles.producto"})
	List<Pedido> findAllByOrderByFechaCreacionDesc();

	@Override
	@EntityGraph(attributePaths = {"detalles", "detalles.producto"})
	Optional<Pedido> findById(Long id);

}
