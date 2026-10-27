package com.tienda.model;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity
@Table(name = "TIENDA_PRODUCTOS")
public class Producto {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tienda_producto_seq")
	@SequenceGenerator(name = "tienda_producto_seq", sequenceName = "TIENDA_PRODUCTO_SEQ", allocationSize = 1)
	@Column(name = "ID")
	private Long id;

	@Column(name = "NOMBRE", nullable = false, length = 120)
	private String nombre;

	@Column(name = "DESCRIPCION", nullable = false, length = 500)
	private String descripcion;

	@Column(name = "PRECIO", nullable = false, precision = 12, scale = 2)
	private BigDecimal precio;

	@Column(name = "STOCK", nullable = false)
	private Integer stock;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public BigDecimal getPrecio() {
		return precio;
	}

	public void setPrecio(BigDecimal precio) {
		this.precio = precio;
	}

	public Integer getStock() {
		return stock;
	}

	public void setStock(Integer stock) {
		this.stock = stock;
	}

}
