package com.tienda.service;

import java.util.List;

import com.tienda.web.dto.LoginRequest;
import com.tienda.web.dto.LoginResponse;
import com.tienda.web.dto.UsuarioActualizacionRequest;
import com.tienda.web.dto.UsuarioRequest;
import com.tienda.web.dto.UsuarioResponse;

public interface UsuarioService {

	List<UsuarioResponse> listar(String token);

	UsuarioResponse obtenerPorId(String token, Long id);

	UsuarioResponse crear(String token, UsuarioRequest request);

	UsuarioResponse actualizar(String token, Long id, UsuarioActualizacionRequest request);

	void eliminar(String token, Long id);

	LoginResponse iniciarSesion(LoginRequest request);

}
