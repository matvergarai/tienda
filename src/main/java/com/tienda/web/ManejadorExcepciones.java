package com.tienda.web;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.tienda.exception.AccesoDenegadoException;
import com.tienda.exception.ConflictoException;
import com.tienda.exception.CredencialesInvalidasException;
import com.tienda.exception.RecursoNoEncontradoException;
import com.tienda.web.dto.ErrorResponse;

@RestControllerAdvice
public class ManejadorExcepciones {

	@ExceptionHandler(RecursoNoEncontradoException.class)
	public ResponseEntity<ErrorResponse> manejarNoEncontrado(RecursoNoEncontradoException ex) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error(HttpStatus.NOT_FOUND, ex.getMessage(), null));
	}

	@ExceptionHandler(CredencialesInvalidasException.class)
	public ResponseEntity<ErrorResponse> manejarCredenciales(CredencialesInvalidasException ex) {
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
				.body(error(HttpStatus.UNAUTHORIZED, ex.getMessage(), null));
	}

	@ExceptionHandler(AccesoDenegadoException.class)
	public ResponseEntity<ErrorResponse> manejarAcceso(AccesoDenegadoException ex) {
		return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error(HttpStatus.FORBIDDEN, ex.getMessage(), null));
	}

	@ExceptionHandler(ConflictoException.class)
	public ResponseEntity<ErrorResponse> manejarConflicto(ConflictoException ex) {
		return ResponseEntity.status(HttpStatus.CONFLICT).body(error(HttpStatus.CONFLICT, ex.getMessage(), null));
	}

	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity<ErrorResponse> manejarIntegridad(DataIntegrityViolationException ex) {
		return ResponseEntity.status(HttpStatus.CONFLICT).body(error(
				HttpStatus.CONFLICT,
				"No se puede completar la operacion porque el registro esta relacionado con otros datos",
				null));
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse> manejarValidacion(MethodArgumentNotValidException ex) {
		Map<String, String> campos = new LinkedHashMap<>();
		ex.getBindingResult().getFieldErrors()
				.forEach(error -> campos.put(error.getField(), error.getDefaultMessage()));
		return ResponseEntity.badRequest()
				.body(error(HttpStatus.BAD_REQUEST, "Los datos enviados no son validos", campos));
	}

	private ErrorResponse error(HttpStatus status, String mensaje, Map<String, String> campos) {
		return new ErrorResponse(status.value(), status.getReasonPhrase(), mensaje, campos);
	}

}
