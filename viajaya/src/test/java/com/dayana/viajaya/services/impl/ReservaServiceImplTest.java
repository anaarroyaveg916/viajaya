package com.dayana.viajaya.services.impl;

import com.dayana.viajaya.dto.IniciarReservaRequest;
import com.dayana.viajaya.dto.IniciarReservaResponse;
import com.dayana.viajaya.entities.BloqueoCupo;
import com.dayana.viajaya.entities.Pasajero;
import com.dayana.viajaya.entities.Reserva;
import com.dayana.viajaya.entities.Usuario;
import com.dayana.viajaya.entities.Vuelo;
import com.dayana.viajaya.repositories.IBloqueoCupoRepository;
import com.dayana.viajaya.repositories.IPasajeroRepository;
import com.dayana.viajaya.repositories.IReservaRepository;
import com.dayana.viajaya.repositories.IUsuarioRepository;
import com.dayana.viajaya.repositories.IVueloRepository;
import com.dayana.viajaya.services.IBloqueoCupoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservaServiceImplTest {

    @Mock
    private IReservaRepository reservaRepository;

    @Mock
    private IVueloRepository vueloRepository;

    @Mock
    private IUsuarioRepository usuarioRepository;

    @Mock
    private IPasajeroRepository pasajeroRepository;

    @Mock
    private IBloqueoCupoRepository bloqueoCupoRepository;

    @Mock
    private IBloqueoCupoService bloqueoCupoService;

    @InjectMocks
    private ReservaServiceImpl reservaService;

    private IniciarReservaRequest solicitud;
    private Usuario usuario;
    private Vuelo vuelo;

    @BeforeEach
    void setUp() {

        solicitud = crearSolicitud();

        usuario = new Usuario();
        usuario.setId(1);
        usuario.setNombre("Usuario");
        usuario.setApellido("Prueba");

        vuelo = new Vuelo();
        vuelo.setId(1);
        vuelo.setAerolinea("Avianca");
        vuelo.setOrigen("Bogota");
        vuelo.setDestino("Medellin");
        vuelo.setFechaSalida(LocalDate.now().plusDays(5));
        vuelo.setHoraSalida(java.time.LocalTime.of(10, 0));
        vuelo.setPrecio(new BigDecimal("189000"));
        vuelo.setCuposDisponibles(12);
        vuelo.setCuposTotales(12);
    }

    @Test
    void debeRechazarSolicitudNula() {

        assertThrows(
                IllegalArgumentException.class,
                () -> reservaService.iniciarReserva(null)
        );

        verifyNoInteractions(reservaRepository);
        verifyNoInteractions(vueloRepository);
        verifyNoInteractions(usuarioRepository);
    }

    @Test
    void debeRechazarIdentificadoresInvalidos() {

        solicitud.setUsuarioId(0);

        IllegalArgumentException excepcion = assertThrows(
                IllegalArgumentException.class,
                () -> reservaService.iniciarReserva(solicitud)
        );

        assertEquals("Debe indicar un usuario válido", excepcion.getMessage());

        verifyNoInteractions(usuarioRepository);
        verifyNoInteractions(vueloRepository);
    }

    @Test
    void debeRechazarCantidadDePasajerosInconsistente() {

        solicitud.setCantidadPasajeros(2);

        IllegalArgumentException excepcion = assertThrows(
                IllegalArgumentException.class,
                () -> reservaService.iniciarReserva(solicitud)
        );

        assertEquals(
                "La cantidad de pasajeros no coincide con los datos registrados",
                excepcion.getMessage()
        );

        verifyNoInteractions(reservaRepository);
        verifyNoInteractions(pasajeroRepository);
    }

    @Test
    void debeRechazarDatosObligatoriosIncompletos() {

        solicitud.getPasajeros().get(0).setNumeroDocumento("");

        IllegalArgumentException excepcion = assertThrows(
                IllegalArgumentException.class,
                () -> reservaService.iniciarReserva(solicitud)
        );

        assertEquals(
                "Todos los datos obligatorios de los pasajeros deben estar completos",
                excepcion.getMessage()
        );

        verifyNoInteractions(reservaRepository);
        verifyNoInteractions(pasajeroRepository);
    }

    @Test
    void debeRechazarFechaDeNacimientoFutura() {

        solicitud.getPasajeros().get(0)
                .setFechaNacimiento(LocalDate.now().plusDays(1));

        IllegalArgumentException excepcion = assertThrows(
                IllegalArgumentException.class,
                () -> reservaService.iniciarReserva(solicitud)
        );

        assertEquals(
                "La fecha de nacimiento no puede ser futura",
                excepcion.getMessage()
        );

        verifyNoInteractions(reservaRepository);
    }

    @Test
    void debeRechazarUsuarioInexistente() {

        when(usuarioRepository.findById(1))
                .thenReturn(Optional.empty());

        IllegalArgumentException excepcion = assertThrows(
                IllegalArgumentException.class,
                () -> reservaService.iniciarReserva(solicitud)
        );

        assertEquals("El usuario indicado no existe", excepcion.getMessage());

        verify(reservaRepository, never()).save(any(Reserva.class));
        verifyNoInteractions(vueloRepository);
    }

    @Test
    void debeRechazarVueloInexistente() {

        when(usuarioRepository.findById(1))
                .thenReturn(Optional.of(usuario));

        when(vueloRepository.findByIdForUpdate(1))
                .thenReturn(Optional.empty());

        IllegalArgumentException excepcion = assertThrows(
                IllegalArgumentException.class,
                () -> reservaService.iniciarReserva(solicitud)
        );

        assertEquals("El vuelo seleccionado no existe", excepcion.getMessage());

        verify(reservaRepository, never()).save(any(Reserva.class));
    }

    @Test
    void debeRechazarVueloSinCuposSuficientes() {

        vuelo.setCuposDisponibles(0);

        when(usuarioRepository.findById(1))
                .thenReturn(Optional.of(usuario));

        when(vueloRepository.findByIdForUpdate(1))
                .thenReturn(Optional.of(vuelo));

        IllegalArgumentException excepcion = assertThrows(
                IllegalArgumentException.class,
                () -> reservaService.iniciarReserva(solicitud)
        );

        assertEquals(
                "El vuelo ya no cuenta con suficientes cupos disponibles",
                excepcion.getMessage()
        );

        verify(reservaRepository, never()).save(any(Reserva.class));
        verify(pasajeroRepository, never()).saveAll(anyList());
        verify(bloqueoCupoRepository, never()).save(any(BloqueoCupo.class));
    }

    @Test
    void debeRechazarVueloConFechaDeSalidaVencida() {

        vuelo.setFechaSalida(LocalDate.now().minusDays(1));

        when(usuarioRepository.findById(1))
                .thenReturn(Optional.of(usuario));

        when(vueloRepository.findByIdForUpdate(1))
                .thenReturn(Optional.of(vuelo));

        IllegalArgumentException excepcion = assertThrows(
                IllegalArgumentException.class,
                () -> reservaService.iniciarReserva(solicitud)
        );

        assertEquals(
                "El vuelo seleccionado ya salió o no está disponible",
                excepcion.getMessage()
        );

        verify(reservaRepository, never()).save(any(Reserva.class));
    }

    @Test
    void debeCrearReservaYBloqueoTemporalCorrectamente() {

        when(usuarioRepository.findById(1))
                .thenReturn(Optional.of(usuario));

        when(vueloRepository.findByIdForUpdate(1))
                .thenReturn(Optional.of(vuelo));

        when(reservaRepository.save(any(Reserva.class)))
                .thenAnswer(invocacion -> {
                    Reserva reserva = invocacion.getArgument(0);
                    reserva.setId(100);
                    return reserva;
                });

        when(pasajeroRepository.saveAll(anyList()))
                .thenAnswer(invocacion -> invocacion.getArgument(0));

        IniciarReservaResponse respuesta =
                reservaService.iniciarReserva(solicitud);

        ArgumentCaptor<Reserva> reservaCaptor =
                ArgumentCaptor.forClass(Reserva.class);

        ArgumentCaptor<List<Pasajero>> pasajerosCaptor =
                ArgumentCaptor.forClass(List.class);

        ArgumentCaptor<BloqueoCupo> bloqueoCaptor =
                ArgumentCaptor.forClass(BloqueoCupo.class);

        verify(reservaRepository).save(reservaCaptor.capture());
        verify(pasajeroRepository).saveAll(pasajerosCaptor.capture());
        verify(vueloRepository).save(vuelo);
        verify(bloqueoCupoRepository).save(bloqueoCaptor.capture());

        Reserva reservaGuardada = reservaCaptor.getValue();
        BloqueoCupo bloqueoGuardado = bloqueoCaptor.getValue();

        assertEquals(100, respuesta.getReservaId());
        assertEquals("PENDIENTE", respuesta.getEstadoReserva());

        assertEquals(new BigDecimal("189000"), respuesta.getPrecioPorPasajero());
        assertEquals(new BigDecimal("189000"), respuesta.getValorTotal());

        assertEquals("PENDIENTE", reservaGuardada.getEstado());
        assertEquals(1, reservaGuardada.getCantidadPasajeros());

        assertEquals(1, pasajerosCaptor.getValue().size());

        assertEquals(11, vuelo.getCuposDisponibles());

        assertEquals(100, bloqueoGuardado.getReservaId());
        assertEquals(1, bloqueoGuardado.getCantidadCupos());
        assertEquals("ACTIVO", bloqueoGuardado.getEstado());


        assertEquals(
                10,
                java.time.Duration.between(
                        bloqueoGuardado.getFechaInicio(),
                        bloqueoGuardado.getFechaVencimiento()
                ).toMinutes()
        );
    }

    private IniciarReservaRequest crearSolicitud() {

        IniciarReservaRequest request = new IniciarReservaRequest();

        request.setVueloId(1);
        request.setUsuarioId(1);
        request.setCantidadPasajeros(1);
        request.setCorreoContacto("prueba@ejemplo.com");
        request.setTelefonoContacto("3001234567");

        IniciarReservaRequest.PasajeroRequest pasajero =
                new IniciarReservaRequest.PasajeroRequest();

        pasajero.setNombres("Carlos");
        pasajero.setApellidos("Prueba");
        pasajero.setTipoDocumento("CC");
        pasajero.setNumeroDocumento("123456789");
        pasajero.setFechaNacimiento(LocalDate.of(1990, 5, 15));

        request.setPasajeros(List.of(pasajero));

        return request;
    }
}
