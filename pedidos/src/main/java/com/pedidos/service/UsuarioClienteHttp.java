package com.pedidos.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

import com.pedidos.exception.AccesoDenegadoException;
import com.pedidos.exception.CredencialesInvalidasException;
import com.pedidos.exception.ServicioNoDisponibleException;

@Component
public class UsuarioClienteHttp implements UsuarioCliente {

	private final RestClient restClient;

	public UsuarioClienteHttp(@Value("${usuarios.servicio.url}") String url) {
		this.restClient = RestClient.builder().baseUrl(url).build();
	}

	@Override
	public UsuarioSesion obtener(String token) {
		if (token == null || token.isBlank()) {
			throw new CredencialesInvalidasException("Debe iniciar sesion");
		}
		try {
			return restClient.get()
					.uri("/usuarios/sesion")
					.header("X-Token", token)
					.retrieve()
					.onStatus(status -> status.value() == 401, (request, response) -> {
						throw new CredencialesInvalidasException("La sesion no es valida");
					})
					.onStatus(status -> status.value() == 403, (request, response) -> {
						throw new AccesoDenegadoException();
					})
					.onStatus(HttpStatusCode::isError, (request, response) -> {
						throw new ServicioNoDisponibleException("El servicio de usuarios no esta disponible");
					})
					.body(UsuarioSesion.class);
		} catch (CredencialesInvalidasException | AccesoDenegadoException | ServicioNoDisponibleException ex) {
			throw ex;
		} catch (ResourceAccessException ex) {
			throw new ServicioNoDisponibleException("El servicio de usuarios no esta disponible");
		}
	}

}
