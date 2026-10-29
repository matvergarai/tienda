package com.usuarios.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.usuarios.exception.AccesoDenegadoException;
import com.usuarios.exception.ConflictoException;
import com.usuarios.exception.CredencialesInvalidasException;
import com.usuarios.mapper.UsuarioMapper;
import com.usuarios.model.Rol;
import com.usuarios.model.Usuario;
import com.usuarios.repository.UsuarioRepository;
import com.usuarios.service.SesionService;
import com.usuarios.web.dto.LoginRequest;
import com.usuarios.web.dto.UsuarioActualizacionRequest;
import com.usuarios.web.dto.UsuarioRequest;
import com.usuarios.web.dto.UsuarioResponse;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceImplTest {

	@Mock
	private UsuarioRepository usuarioRepository;

	private SesionService sesionService;
	private UsuarioServiceImpl service;
	private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

	@BeforeEach
	void setUp() {
		sesionService = new SesionService(usuarioRepository);
		service = new UsuarioServiceImpl(usuarioRepository, new UsuarioMapper(), passwordEncoder, sesionService);
	}

	@Test
	void elRegistroPublicoQuedaComoClienteAunquePidaSerAdmin() {
		when(usuarioRepository.existsByCorreo("ana@tienda.cl")).thenReturn(false);
		when(usuarioRepository.save(any())).thenAnswer(invocacion -> {
			Usuario usuario = invocacion.getArgument(0);
			usuario.setId(2L);
			return usuario;
		});

		UsuarioResponse respuesta = service.crear(null, new UsuarioRequest(
				"Ana Perez", "ana@tienda.cl", "secreto", Rol.ADMINISTRADOR));

		assertEquals(Rol.CLIENTE, respuesta.rol());
		assertEquals("ana@tienda.cl", respuesta.correo());
	}

	@Test
	void unAdministradorPuedeCrearOtroAdministrador() {
		Usuario admin = usuario(1L, Rol.ADMINISTRADOR);
		when(usuarioRepository.findById(1L)).thenReturn(Optional.of(admin));
		when(usuarioRepository.existsByCorreo("otro@tienda.cl")).thenReturn(false);
		when(usuarioRepository.save(any())).thenAnswer(invocacion -> {
			Usuario usuario = invocacion.getArgument(0);
			usuario.setId(3L);
			return usuario;
		});

		UsuarioResponse respuesta = service.crear(sesionService.abrir(admin), new UsuarioRequest(
				"Otro Admin", "otro@tienda.cl", "secreto", Rol.ADMINISTRADOR));

		assertEquals(Rol.ADMINISTRADOR, respuesta.rol());
	}

	@Test
	void noPermiteUnCorreoRepetido() {
		when(usuarioRepository.existsByCorreo("ana@tienda.cl")).thenReturn(true);

		assertThrows(ConflictoException.class, () -> service.crear(null, new UsuarioRequest(
				"Ana Perez", "Ana@tienda.cl", "secreto", null)));
	}

	@Test
	void iniciaSesionYRechazaLaClaveIncorrecta() {
		Usuario cliente = usuario(2L, Rol.CLIENTE);
		cliente.setClave(passwordEncoder.encode("secreto"));
		when(usuarioRepository.findByCorreo("ana@tienda.cl")).thenReturn(Optional.of(cliente));

		assertEquals(2L, service.iniciarSesion(new LoginRequest("ana@tienda.cl", "secreto")).id());
		assertThrows(CredencialesInvalidasException.class,
				() -> service.iniciarSesion(new LoginRequest("ana@tienda.cl", "mala")));
	}

	@Test
	void unClienteNoPuedeListarNiEliminarUsuarios() {
		Usuario cliente = usuario(2L, Rol.CLIENTE);
		when(usuarioRepository.findById(2L)).thenReturn(Optional.of(cliente));
		String token = sesionService.abrir(cliente);

		assertThrows(AccesoDenegadoException.class, () -> service.listar(token));
		assertThrows(AccesoDenegadoException.class, () -> service.eliminar(token, 1L));
		verify(usuarioRepository, never()).delete(any());
	}

	@Test
	void unAdministradorListaYNoPuedeBorrarAlUnicoAdmin() {
		Usuario admin = usuario(1L, Rol.ADMINISTRADOR);
		when(usuarioRepository.findById(1L)).thenReturn(Optional.of(admin));
		when(usuarioRepository.findAll()).thenReturn(List.of(admin));
		when(usuarioRepository.countByRol(Rol.ADMINISTRADOR)).thenReturn(1L);
		String token = sesionService.abrir(admin);

		assertEquals(1, service.listar(token).size());
		assertThrows(ConflictoException.class, () -> service.eliminar(token, 1L));
	}

	@Test
	void unClienteNoPuedeCambiarSuRol() {
		Usuario cliente = usuario(2L, Rol.CLIENTE);
		when(usuarioRepository.findById(2L)).thenReturn(Optional.of(cliente));
		when(usuarioRepository.existsByCorreoAndIdNot("ana@tienda.cl", 2L)).thenReturn(false);
		String token = sesionService.abrir(cliente);

		assertThrows(AccesoDenegadoException.class, () -> service.actualizar(
				token,
				2L,
				new UsuarioActualizacionRequest("Ana Perez", "ana@tienda.cl", null, Rol.ADMINISTRADOR)));
	}

	private Usuario usuario(Long id, Rol rol) {
		Usuario usuario = new Usuario();
		usuario.setId(id);
		usuario.setNombre(rol == Rol.ADMINISTRADOR ? "Administrador" : "Ana Perez");
		usuario.setCorreo(rol == Rol.ADMINISTRADOR ? "admin@tienda.cl" : "ana@tienda.cl");
		usuario.setClave("hash");
		usuario.setRol(rol);
		return usuario;
	}

}
