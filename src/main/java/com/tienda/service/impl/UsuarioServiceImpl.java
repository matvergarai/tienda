package com.tienda.service.impl;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tienda.exception.AccesoDenegadoException;
import com.tienda.exception.ConflictoException;
import com.tienda.exception.CredencialesInvalidasException;
import com.tienda.exception.RecursoNoEncontradoException;
import com.tienda.mapper.UsuarioMapper;
import com.tienda.model.Rol;
import com.tienda.model.Usuario;
import com.tienda.repository.UsuarioRepository;
import com.tienda.service.SesionService;
import com.tienda.service.UsuarioService;
import com.tienda.web.dto.LoginRequest;
import com.tienda.web.dto.LoginResponse;
import com.tienda.web.dto.UsuarioActualizacionRequest;
import com.tienda.web.dto.UsuarioRequest;
import com.tienda.web.dto.UsuarioResponse;

@Service
public class UsuarioServiceImpl implements UsuarioService {

	private final UsuarioRepository usuarioRepository;
	private final UsuarioMapper usuarioMapper;
	private final PasswordEncoder passwordEncoder;
	private final SesionService sesionService;

	public UsuarioServiceImpl(
			UsuarioRepository usuarioRepository,
			UsuarioMapper usuarioMapper,
			PasswordEncoder passwordEncoder,
			SesionService sesionService) {
		this.usuarioRepository = usuarioRepository;
		this.usuarioMapper = usuarioMapper;
		this.passwordEncoder = passwordEncoder;
		this.sesionService = sesionService;
	}

	@Override
	@Transactional(readOnly = true)
	public List<UsuarioResponse> listar(String token) {
		sesionService.exigirRol(token, Rol.ADMINISTRADOR);
		return usuarioRepository.findAll().stream()
				.map(usuarioMapper::toResponse)
				.toList();
	}

	@Override
	@Transactional(readOnly = true)
	public UsuarioResponse obtenerPorId(String token, Long id) {
		sesionService.exigirMismoOAdmin(token, id);
		return usuarioMapper.toResponse(buscar(id));
	}

	@Override
	@Transactional
	public UsuarioResponse crear(String token, UsuarioRequest request) {
		Rol rol = Rol.CLIENTE;
		if (token != null && !token.isBlank()) {
			sesionService.exigirRol(token, Rol.ADMINISTRADOR);
			if (request.rol() != null) {
				rol = request.rol();
			}
		}
		String correo = normalizarCorreo(request.correo());
		if (usuarioRepository.existsByCorreo(correo)) {
			throw new ConflictoException("Ya existe un usuario con el correo " + correo);
		}
		Usuario usuario = new Usuario();
		usuario.setNombre(request.nombre().trim());
		usuario.setCorreo(correo);
		usuario.setClave(passwordEncoder.encode(request.clave()));
		usuario.setRol(rol);
		return usuarioMapper.toResponse(usuarioRepository.save(usuario));
	}

	@Override
	@Transactional
	public UsuarioResponse actualizar(String token, Long id, UsuarioActualizacionRequest request) {
		Usuario actor = sesionService.exigirMismoOAdmin(token, id);
		Usuario usuario = buscar(id);
		String correo = normalizarCorreo(request.correo());
		if (usuarioRepository.existsByCorreoAndIdNot(correo, id)) {
			throw new ConflictoException("Ya existe un usuario con el correo " + correo);
		}
		if (request.rol() != null && request.rol() != usuario.getRol() && actor.getRol() != Rol.ADMINISTRADOR) {
			throw new AccesoDenegadoException("Solo un administrador puede cambiar el rol");
		}
		usuario.setNombre(request.nombre().trim());
		usuario.setCorreo(correo);
		if (request.clave() != null && !request.clave().isBlank()) {
			usuario.setClave(passwordEncoder.encode(request.clave()));
		}
		if (actor.getRol() == Rol.ADMINISTRADOR && request.rol() != null) {
			usuario.setRol(request.rol());
		}
		return usuarioMapper.toResponse(usuarioRepository.save(usuario));
	}

	@Override
	@Transactional
	public void eliminar(String token, Long id) {
		sesionService.exigirRol(token, Rol.ADMINISTRADOR);
		Usuario usuario = buscar(id);
		if (usuario.getRol() == Rol.ADMINISTRADOR && usuarioRepository.countByRol(Rol.ADMINISTRADOR) <= 1) {
			throw new ConflictoException("Debe quedar al menos un administrador");
		}
		usuarioRepository.delete(usuario);
	}

	@Override
	@Transactional(readOnly = true)
	public LoginResponse iniciarSesion(LoginRequest request) {
		Usuario usuario = usuarioRepository.findByCorreo(normalizarCorreo(request.correo()))
				.filter(encontrado -> passwordEncoder.matches(request.clave(), encontrado.getClave()))
				.orElseThrow(() -> new CredencialesInvalidasException("Correo o clave incorrectos"));
		return usuarioMapper.toLogin(usuario, sesionService.abrir(usuario));
	}

	private Usuario buscar(Long id) {
		return usuarioRepository.findById(id)
				.orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado con id " + id));
	}

	private String normalizarCorreo(String correo) {
		return correo.trim().toLowerCase();
	}

}
