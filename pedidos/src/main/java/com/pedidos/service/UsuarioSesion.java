package com.pedidos.service;

import com.pedidos.model.Rol;

public record UsuarioSesion(Long id, String nombre, String correo, Rol rol) {
}
