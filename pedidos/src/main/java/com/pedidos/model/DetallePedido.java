package com.pedidos.model;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity
@Table(name = "TIENDA_DETALLE_PEDIDOS")
public class DetallePedido {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tienda_detalle_seq")
	@SequenceGenerator(name = "tienda_detalle_seq", sequenceName = "TIENDA_DETALLE_SEQ", allocationSize = 1)
	@Column(name = "ID")
	private Long id;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "PEDIDO_ID", nullable = false)
	private Pedido pedido;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "PRODUCTO_ID", nullable = false)
	private Producto producto;

	@Column(name = "CANTIDAD", nullable = false)
	private Integer cantidad;

	@Column(name = "PRECIO_UNITARIO", nullable = false, precision = 12, scale = 2)
	private BigDecimal precioUnitario;

	@Column(name = "SUBTOTAL", nullable = false, precision = 12, scale = 2)
	private BigDecimal subtotal;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Pedido getPedido() {
		return pedido;
	}

	public void setPedido(Pedido pedido) {
		this.pedido = pedido;
	}

	public Producto getProducto() {
		return producto;
	}

	public void setProducto(Producto producto) {
		this.producto = producto;
	}

	public Integer getCantidad() {
		return cantidad;
	}

	public void setCantidad(Integer cantidad) {
		this.cantidad = cantidad;
	}

	public BigDecimal getPrecioUnitario() {
		return precioUnitario;
	}

	public void setPrecioUnitario(BigDecimal precioUnitario) {
		this.precioUnitario = precioUnitario;
	}

	public BigDecimal getSubtotal() {
		return subtotal;
	}

	public void setSubtotal(BigDecimal subtotal) {
		this.subtotal = subtotal;
	}

}
