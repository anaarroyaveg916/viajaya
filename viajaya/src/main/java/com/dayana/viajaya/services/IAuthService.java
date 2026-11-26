package com.dayana.viajaya.services;

import com.dayana.viajaya.dto.LoginRequest;
import com.dayana.viajaya.dto.LoginResponse;
import com.dayana.viajaya.dto.LogoutResponse;
import com.dayana.viajaya.entities.Usuario;

import java.util.Optional;

public interface IAuthService {
    LoginResponse login(LoginRequest request);
    LogoutResponse logout(String token);
    Optional<Usuario> validarSesionActiva(String token);
}