package com.tienda.web;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.tienda.service.UsuarioService;
import com.tienda.web.dto.LoginRequest;
import com.tienda.web.dto.LoginResponse;
import com.tienda.web.dto.UsuarioActualizacionRequest;
import com.tienda.web.dto.UsuarioRequest;
import com.tienda.web.dto.UsuarioResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

	private final UsuarioService usuarioService;

	public UsuarioController(UsuarioService usuarioService) {
		this.usuarioService = usuarioService;
	}

	@GetMapping
	public List<UsuarioResponse> listar(@RequestHeader(value = "X-Token", required = false) String token) {
		return usuarioService.listar(token);
	}

	@GetMapping("/{id}")
	public UsuarioResponse obtenerPorId(
			@RequestHeader(value = "X-Token", required = false) String token,
			@PathVariable Long id) {
		return usuarioService.obtenerPorId(token, id);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public UsuarioResponse crear(
			@RequestHeader(value = "X-Token", required = false) String token,
			@Valid @RequestBody UsuarioRequest request) {
		return usuarioService.crear(token, request);
	}

	@PutMapping("/{id}")
	public UsuarioResponse actualizar(
			@RequestHeader(value = "X-Token", required = false) String token,
			@PathVariable Long id,
			@Valid @RequestBody UsuarioActualizacionRequest request) {
		return usuarioService.actualizar(token, id, request);
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void eliminar(
			@RequestHeader(value = "X-Token", required = false) String token,
			@PathVariable Long id) {
		usuarioService.eliminar(token, id);
	}

	@PostMapping("/login")
	public LoginResponse iniciarSesion(@Valid @RequestBody LoginRequest request) {
		return usuarioService.iniciarSesion(request);
	}

}
