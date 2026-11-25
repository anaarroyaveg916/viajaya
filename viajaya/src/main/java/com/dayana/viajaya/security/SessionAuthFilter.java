package com.dayana.viajaya.security;

import com.dayana.viajaya.entities.Usuario;
import com.dayana.viajaya.services.IAuthService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class SessionAuthFilter extends OncePerRequestFilter {

    private final IAuthService authService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String path = request.getRequestURI();

        if (!esRutaProtegida(path)) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = extraerToken(request);
        Optional<Usuario> usuarioOpt = authService.validarSesionActiva(token);

        if (usuarioOpt.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write("{\"error\":\"Sesión no válida o finalizada. Inicie sesión nuevamente.\"}");
            return;
        }

        request.setAttribute("usuarioAutenticado", usuarioOpt.get());
        filterChain.doFilter(request, response);
    }

    private boolean esRutaProtegida(String path) {
        if (path == null) {
            return true;
        }
        if (path.equals("/api/auth/login") || path.startsWith("/api/vuelos")) {
            return false;
        }
        return path.startsWith("/api/usuarios") || path.startsWith("/api/reservas");
    }

    private String extraerToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && !authHeader.isBlank()) {
            return authHeader;
        }
        return request.getHeader("X-Session-Token");
    }
}