package com.tienda.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import com.tienda.exception.AccesoDenegadoException;
import com.tienda.exception.CredencialesInvalidasException;
import com.tienda.model.Rol;
import com.tienda.service.UsuarioService;
import com.tienda.web.dto.LoginResponse;
import com.tienda.web.dto.UsuarioResponse;

@ExtendWith(MockitoExtension.class)
class UsuarioControllerTest {

	@Mock
	private UsuarioService usuarioService;

	private MockMvc mockMvc;

	@BeforeEach
	void setUp() {
		LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
		validator.afterPropertiesSet();
		mockMvc = MockMvcBuilders.standaloneSetup(new UsuarioController(usuarioService))
				.setControllerAdvice(new ManejadorExcepciones())
				.setValidator(validator)
				.build();
	}

	@Test
	void registraUnCliente() throws Exception {
		when(usuarioService.crear(isNull(), any())).thenReturn(new UsuarioResponse(2L, "Ana Perez", "ana@tienda.cl", Rol.CLIENTE));

		mockMvc.perform(post("/usuarios")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{"nombre":"Ana Perez","correo":"ana@tienda.cl","clave":"secreto"}
						"""))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.rol").value("CLIENTE"))
				.andExpect(jsonPath("$.clave").doesNotExist());
	}

	@Test
	void iniciaSesion() throws Exception {
		when(usuarioService.iniciarSesion(any()))
				.thenReturn(new LoginResponse(1L, "Administrador", "admin@tienda.cl", Rol.ADMINISTRADOR, "token-1"));

		mockMvc.perform(post("/usuarios/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{"correo":"admin@tienda.cl","clave":"Admin12345"}
						"""))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.token").value("token-1"));
	}

	@Test
	void responde401SiLaClaveNoCoincide() throws Exception {
		when(usuarioService.iniciarSesion(any()))
				.thenThrow(new CredencialesInvalidasException("Correo o clave incorrectos"));

		mockMvc.perform(post("/usuarios/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{"correo":"ana@tienda.cl","clave":"mala"}
						"""))
				.andExpect(status().isUnauthorized());
	}

	@Test
	void responde403SiUnClienteListaUsuarios() throws Exception {
		when(usuarioService.listar(eq("token-cliente"))).thenThrow(new AccesoDenegadoException());

		mockMvc.perform(get("/usuarios").header("X-Token", "token-cliente"))
				.andExpect(status().isForbidden());
	}

	@Test
	void actualizaYEliminaConToken() throws Exception {
		when(usuarioService.actualizar(eq("token-admin"), eq(2L), any()))
				.thenReturn(new UsuarioResponse(2L, "Ana Perez", "ana@tienda.cl", Rol.CLIENTE));

		mockMvc.perform(put("/usuarios/2")
				.header("X-Token", "token-admin")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{"nombre":"Ana Perez","correo":"ana@tienda.cl"}
						"""))
				.andExpect(status().isOk());

		mockMvc.perform(delete("/usuarios/2").header("X-Token", "token-admin"))
				.andExpect(status().isNoContent());
	}

	@Test
	void responde400SiFaltanDatos() throws Exception {
		mockMvc.perform(post("/usuarios")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{}"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.campos.correo").exists());
	}

}
