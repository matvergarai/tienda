package com.tienda.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity
@Table(name = "TIENDA_USUARIOS")
public class Usuario {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tienda_usuario_seq")
	@SequenceGenerator(name = "tienda_usuario_seq", sequenceName = "TIENDA_USUARIO_SEQ", allocationSize = 1)
	@Column(name = "ID")
	private Long id;

	@Column(name = "NOMBRE", nullable = false, length = 120)
	private String nombre;

	@Column(name = "CORREO", nullable = false, unique = true, length = 150)
	private String correo;

	@Column(name = "CLAVE", nullable = false, length = 100)
	private String clave;

	@Enumerated(EnumType.STRING)
	@Column(name = "ROL", nullable = false, length = 20)
	private Rol rol;

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

	public String getCorreo() {
		return correo;
	}

	public void setCorreo(String correo) {
		this.correo = correo;
	}

	public String getClave() {
		return clave;
	}

	public void setClave(String clave) {
		this.clave = clave;
	}

	public Rol getRol() {
		return rol;
	}

	public void setRol(Rol rol) {
		this.rol = rol;
	}

}
