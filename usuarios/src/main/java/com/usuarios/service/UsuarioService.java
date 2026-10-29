package com.usuarios.service;

import java.util.List;

import com.usuarios.web.dto.LoginRequest;
import com.usuarios.web.dto.LoginResponse;
import com.usuarios.web.dto.UsuarioActualizacionRequest;
import com.usuarios.web.dto.UsuarioRequest;
import com.usuarios.web.dto.UsuarioResponse;

public interface UsuarioService {

	List<UsuarioResponse> listar(String token);

	UsuarioResponse obtenerPorId(String token, Long id);

	UsuarioResponse crear(String token, UsuarioRequest request);

	UsuarioResponse actualizar(String token, Long id, UsuarioActualizacionRequest request);

	void eliminar(String token, Long id);

	LoginResponse iniciarSesion(LoginRequest request);

	UsuarioResponse sesion(String token);

}
