package com.tienda.exception;

public class AccesoDenegadoException extends RuntimeException {

	public AccesoDenegadoException() {
		super("No tiene permiso para realizar esta accion");
	}

	public AccesoDenegadoException(String mensaje) {
		super(mensaje);
	}

}
