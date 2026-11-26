package com.dayana.viajaya.services.impl;

import com.dayana.viajaya.dto.LoginRequest;
import com.dayana.viajaya.dto.LoginResponse;
import com.dayana.viajaya.dto.LogoutResponse;
import com.dayana.viajaya.entities.Usuario;
import com.dayana.viajaya.repositories.IUsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private IUsuarioRepository usuarioRepository;

    private PasswordEncoder passwordEncoder;
    private AuthServiceImpl authService;
    private Usuario usuarioActivo;

    @BeforeEach
    void setUp() {
        passwordEncoder = new BCryptPasswordEncoder();
        authService = new AuthServiceImpl(usuarioRepository, passwordEncoder);

        usuarioActivo = new Usuario();
        usuarioActivo.setId(1);
        usuarioActivo.setNombre("Laura");
        usuarioActivo.setApellido("Gomez");
        usuarioActivo.setCorreo("laura@gmail.com");
        usuarioActivo.setTelefono("3001234567");
        usuarioActivo.setPasswordHash(passwordEncoder.encode("ClaveSegura123!"));
    }

    @Test
    void debeIniciarSesionConCredencialesValidas() {
        when(usuarioRepository.findByCorreoIgnoreCase("laura@gmail.com"))
                .thenReturn(Optional.of(usuarioActivo));

        LoginRequest request = new LoginRequest();
        request.setCorreo("laura@gmail.com");
        request.setPassword("ClaveSegura123!");

        LoginResponse response = authService.login(request);

        assertNotNull(response.getToken());
        assertEquals(1, response.getUsuarioId());
        assertEquals("laura@gmail.com", response.getCorreo());
        assertTrue(authService.validarSesionActiva(response.getToken()).isPresent());
    }

    @Test
    void debeRechazarConMensajeGenericoCuandoCorreoNoExiste() {
        when(usuarioRepository.findByCorreoIgnoreCase("noexiste@gmail.com"))
                .thenReturn(Optional.empty());

        LoginRequest request = new LoginRequest();
        request.setCorreo("noexiste@gmail.com");
        request.setPassword("CualquierClave123!");

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> authService.login(request)
        );
        assertEquals("Credenciales inválidas", ex.getMessage());
    }

    @Test
    void debeRechazarConMensajeGenericoCuandoContrasenaEsIncorrecta() {
        when(usuarioRepository.findByCorreoIgnoreCase("laura@gmail.com"))
                .thenReturn(Optional.of(usuarioActivo));

        LoginRequest request = new LoginRequest();
        request.setCorreo("laura@gmail.com");
        request.setPassword("ClaveErronea!");

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> authService.login(request)
        );
        assertEquals("Credenciales inválidas", ex.getMessage());
    }

    @Test
    void debeCerrarSesionEInvalidarAccesoPosterior() {
        when(usuarioRepository.findByCorreoIgnoreCase("laura@gmail.com"))
                .thenReturn(Optional.of(usuarioActivo));
        when(usuarioRepository.findById(1))
                .thenReturn(Optional.of(usuarioActivo));

        LoginRequest request = new LoginRequest();
        request.setCorreo("laura@gmail.com");
        request.setPassword("ClaveSegura123!");
        LoginResponse loginResponse = authService.login(request);

        LogoutResponse logoutResponse = authService.logout("Bearer " + loginResponse.getToken());

        assertTrue(logoutResponse.isSesionCerrada());
        assertTrue(authService.validarSesionActiva(loginResponse.getToken()).isEmpty());
    }
}