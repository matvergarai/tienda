package com.usuarios.service;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

import com.usuarios.exception.AccesoDenegadoException;
import com.usuarios.exception.CredencialesInvalidasException;
import com.usuarios.model.Rol;
import com.usuarios.model.Usuario;
import com.usuarios.repository.UsuarioRepository;

@Service
public class SesionService {

	private final UsuarioRepository usuarioRepository;
	private final ConcurrentHashMap<String, Long> sesiones = new ConcurrentHashMap<>();

	public SesionService(UsuarioRepository usuarioRepository) {
		this.usuarioRepository = usuarioRepository;
	}

	public String abrir(Usuario usuario) {
		String token = UUID.randomUUID().toString();
		sesiones.put(token, usuario.getId());
		return token;
	}

	public Usuario exigir(String token) {
		if (token == null || token.isBlank()) {
			throw new CredencialesInvalidasException("Debe iniciar sesion");
		}
		Long id = sesiones.get(token.trim());
		if (id == null) {
			throw new CredencialesInvalidasException("La sesion no es valida");
		}
		return usuarioRepository.findById(id)
				.orElseThrow(() -> new CredencialesInvalidasException("La sesion no es valida"));
	}

	public Usuario exigirRol(String token, Rol rol) {
		Usuario usuario = exigir(token);
		if (usuario.getRol() != rol) {
			throw new AccesoDenegadoException();
		}
		return usuario;
	}

	public Usuario exigirMismoOAdmin(String token, Long objetivoId) {
		Usuario usuario = exigir(token);
		if (usuario.getRol() == Rol.ADMINISTRADOR || usuario.getId().equals(objetivoId)) {
			return usuario;
		}
		throw new AccesoDenegadoException();
	}

}
