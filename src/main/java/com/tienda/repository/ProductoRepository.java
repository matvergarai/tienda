package com.tienda.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.tienda.model.Producto;

public interface ProductoRepository extends JpaRepository<Producto, Long> {

	@Query("""
			select p from Producto p
			where :texto is null or :texto = ''
				or lower(p.nombre) like lower(concat('%', :texto, '%'))
				or lower(p.descripcion) like lower(concat('%', :texto, '%'))
			order by p.nombre
			""")
	List<Producto> buscar(@Param("texto") String texto);

}
