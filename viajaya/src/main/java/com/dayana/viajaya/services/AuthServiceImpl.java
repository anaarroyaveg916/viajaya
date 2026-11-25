package com.dayana.viajaya.services.impl;

import com.dayana.viajaya.dto.LoginRequest;
import com.dayana.viajaya.dto.LoginResponse;
import com.dayana.viajaya.dto.LogoutResponse;
import com.dayana.viajaya.entities.Usuario;
import com.dayana.viajaya.repositories.IUsuarioRepository;
import com.dayana.viajaya.services.IAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements IAuthService {

    private static final String MENSAJE_CREDENCIALES_INVALIDAS = "Credenciales inválidas";

    private final IUsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    // Almacén de sesiones activas en memoria (token -> usuarioId)
    private final Map<String, Integer> sesionesActivas = new ConcurrentHashMap<>();

    @Override
    public LoginResponse login(LoginRequest request) {
        if (request == null
                || request.getCorreo() == null || request.getCorreo().isBlank()
                || request.getPassword() == null || request.getPassword().isBlank()) {
            throw new IllegalArgumentException(MENSAJE_CREDENCIALES_INVALIDAS);
        }

        String correoNormalizado = request.getCorreo().trim().toLowerCase();

        Usuario usuario = usuarioRepository.findByCorreoIgnoreCase(correoNormalizado)
                .orElseThrow(() -> new IllegalArgumentException(MENSAJE_CREDENCIALES_INVALIDAS));

        if (usuario.getPasswordHash() == null || usuario.getPasswordHash().isBlank()) {
            throw new IllegalArgumentException(MENSAJE_CREDENCIALES_INVALIDAS);
        }

        if (!passwordEncoder.matches(request.getPassword(), usuario.getPasswordHash())) {
            throw new IllegalArgumentException(MENSAJE_CREDENCIALES_INVALIDAS);
        }

        String token = UUID.randomUUID().toString();
        sesionesActivas.put(token, usuario.getId());

        return new LoginResponse(
                token,
                usuario.getId(),
                usuario.getNombre(),
                usuario.getApellido(),
                usuario.getCorreo(),
                "Inicio de sesión exitoso"
        );
    }

    @Override
    public LogoutResponse logout(String token) {
        String tokenLimpio = normalizarToken(token);
        if (tokenLimpio == null || !sesionesActivas.containsKey(tokenLimpio)) {
            throw new IllegalArgumentException("No existe una sesión activa para cerrar");
        }

        sesionesActivas.remove(tokenLimpio);
        return new LogoutResponse(true, "Sesión cerrada correctamente");
    }

    @Override
    public Optional<Usuario> validarSesionActiva(String token) {
        String tokenLimpio = normalizarToken(token);
        if (tokenLimpio == null) {
            return Optional.empty();
        }

        Integer usuarioId = sesionesActivas.get(tokenLimpio);
        if (usuarioId == null) {
            return Optional.empty();
        }

        return usuarioRepository.findById(usuarioId);
    }

    private String normalizarToken(String token) {
        if (token == null || token.isBlank()) {
            return null;
        }
        String valor = token.trim();
        if (valor.toLowerCase().startsWith("bearer ")) {
            valor = valor.substring(7).trim();
        }
        return valor.isEmpty() ? null : valor;
    }
}