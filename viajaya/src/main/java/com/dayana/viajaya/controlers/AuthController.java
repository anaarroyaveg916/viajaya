package com.dayana.viajaya.controllers;

import com.dayana.viajaya.dto.LoginRequest;
import com.dayana.viajaya.dto.LoginResponse;
import com.dayana.viajaya.dto.LogoutResponse;
import com.dayana.viajaya.entities.Usuario;
import com.dayana.viajaya.services.IAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final IAuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<LogoutResponse> logout(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @RequestHeader(value = "X-Session-Token", required = false) String sessionToken
    ) {
        String token = (authorization != null && !authorization.isBlank()) ? authorization : sessionToken;
        LogoutResponse response = authService.logout(token);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/sesion")
    public ResponseEntity<?> estadoSesion(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @RequestHeader(value = "X-Session-Token", required = false) String sessionToken
    ) {
        String token = (authorization != null && !authorization.isBlank()) ? authorization : sessionToken;
        Optional<Usuario> usuarioOpt = authService.validarSesionActiva(token);
        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("activa", false, "mensaje", "Sesión no activa"));
        }
        Usuario u = usuarioOpt.get();
        return ResponseEntity.ok(Map.of(
                "activa", true,
                "usuarioId", u.getId(),
                "correo", u.getCorreo()
        ));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity> manejarErrorAutenticacion(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity> manejarErrorGeneral(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "No fue posible procesar la solicitud de autenticación"));
    }
}