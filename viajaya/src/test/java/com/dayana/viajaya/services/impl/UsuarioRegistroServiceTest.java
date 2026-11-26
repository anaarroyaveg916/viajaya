package com.dayana.viajaya.services.impl;

import com.dayana.viajaya.dto.RegistroUsuarioRequest;
import com.dayana.viajaya.dto.RegistroUsuarioResponse;
import com.dayana.viajaya.entities.Usuario;
import com.dayana.viajaya.exceptions.RegistroValidacionException;
import com.dayana.viajaya.repositories.IUsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioRegistroServiceTest {

    @Mock
    private IUsuarioRepository usuarioRepository;

    private PasswordEncoder passwordEncoder;
    private UsuarioServiceImpl usuarioService;

    @BeforeEach
    void setUp() {
        passwordEncoder = new BCryptPasswordEncoder();
        usuarioService = new UsuarioServiceImpl(usuarioRepository, passwordEncoder);
    }

    @Test
    void debeRegistrarUsuarioConDatosValidosYHashearContrasena() {
        when(usuarioRepository.existsByCorreoIgnoreCase("ana@correo.com")).thenReturn(false);
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(inv -> {
            Usuario u = inv.getArgument(0);
            u.setId(10);
            return u;
        });

        RegistroUsuarioRequest req = new RegistroUsuarioRequest();
        req.setNombre("Ana");
        req.setApellido("Lopez");
        req.setCorreo("ANA@CORREO.COM");
        req.setTelefono("3009998877");
        req.setPassword("ViajaYa123");

        RegistroUsuarioResponse resp = usuarioService.registrar(req);

        ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioRepository).save(captor.capture());
        Usuario guardado = captor.getValue();

        assertEquals(10, resp.getId());
        assertEquals("ana@correo.com", resp.getCorreo());
        assertEquals("Cuenta creada exitosamente", resp.getMensaje());
        assertNotEquals("ViajaYa123", guardado.getPasswordHash());
        assertTrue(passwordEncoder.matches("ViajaYa123", guardado.getPasswordHash()));
    }

    @Test
    void debeReportarErroresPorCampoCuandoFaltanObligatorios() {
        RegistroUsuarioRequest req = new RegistroUsuarioRequest();
        req.setNombre(" ");
        req.setCorreo("");
        req.setPassword("");

        RegistroValidacionException ex = assertThrows(
                RegistroValidacionException.class,
                () -> usuarioService.registrar(req)
        );

        assertTrue(ex.getErroresPorCampo().containsKey("nombre"));
        assertTrue(ex.getErroresPorCampo().containsKey("correo"));
        assertTrue(ex.getErroresPorCampo().containsKey("password"));
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void debeRechazarCorreoInvalido() {
        RegistroUsuarioRequest req = new RegistroUsuarioRequest();
        req.setNombre("Ana");
        req.setCorreo("correo-sin-arroba");
        req.setPassword("ViajaYa123");

        RegistroValidacionException ex = assertThrows(
                RegistroValidacionException.class,
                () -> usuarioService.registrar(req)
        );

        assertTrue(ex.getErroresPorCampo().containsKey("correo"));
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void debeRechazarCorreoDuplicado() {
        when(usuarioRepository.existsByCorreoIgnoreCase("laura@gmail.com")).thenReturn(true);

        RegistroUsuarioRequest req = new RegistroUsuarioRequest();
        req.setNombre("Laura");
        req.setCorreo("laura@gmail.com");
        req.setPassword("ViajaYa123");

        RegistroValidacionException ex = assertThrows(
                RegistroValidacionException.class,
                () -> usuarioService.registrar(req)
        );

        assertTrue(ex.getErroresPorCampo().containsKey("correo"));
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void debeRechazarContrasenaQueNoCumplePoliticaMinima() {
        when(usuarioRepository.existsByCorreoIgnoreCase("ana@correo.com")).thenReturn(false);

        RegistroUsuarioRequest req = new RegistroUsuarioRequest();
        req.setNombre("Ana");
        req.setCorreo("ana@correo.com");
        req.setPassword("soloTexto");

        RegistroValidacionException ex = assertThrows(
                RegistroValidacionException.class,
                () -> usuarioService.registrar(req)
        );

        assertTrue(ex.getErroresPorCampo().containsKey("password"));
        verify(usuarioRepository, never()).save(any());
    }
}