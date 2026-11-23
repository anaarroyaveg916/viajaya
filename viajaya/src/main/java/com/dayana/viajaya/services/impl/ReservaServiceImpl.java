
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
import com.dayana.viajaya.services.IReservaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservaServiceImpl implements IReservaService {

    private static final String ESTADO_PENDIENTE = "PENDIENTE";
    private static final String ESTADO_BLOQUEO_ACTIVO = "ACTIVO";
    private static final int MINUTOS_BLOQUEO = 10;

    private final IReservaRepository reservaRepository;
    private final IVueloRepository vueloRepository;
    private final IUsuarioRepository usuarioRepository;
    private final IPasajeroRepository pasajeroRepository;
    private final IBloqueoCupoRepository bloqueoCupoRepository;
    private final IBloqueoCupoService bloqueoCupoService;

    // Consultar todas las reservas
    @Override
    public List<Reserva> findAll() {
        return reservaRepository.findAll();
    }

    // Consultar una reserva por su identificador
    @Override
    public Reserva findById(Integer id) {
        return reservaRepository.findById(id).orElse(null);
    }

    // Consultar reservas de un usuario
    @Override
    public List<Reserva> findByUsuarioId(Integer usuarioId) {
        return reservaRepository.findByUsuarioId(usuarioId);
    }

    // Guardar una reserva
    @Override
    public Reserva save(Reserva reserva) {
        return reservaRepository.save(reserva);
    }

    // Actualizar una reserva
    @Override
    public Reserva update(Integer id, Reserva reserva) {

        Reserva reservaDb = findById(id);

        if (reservaDb == null) {
            return null;
        }

        reservaDb.setFechaReserva(reserva.getFechaReserva());
        reservaDb.setDestino(reserva.getDestino());
        reservaDb.setValor(reserva.getValor());
        reservaDb.setEstado(reserva.getEstado());
        reservaDb.setUsuarioId(reserva.getUsuarioId());

        return reservaRepository.save(reservaDb);
    }

    // Actualización parcial de una reserva
    @Override
    public Reserva patch(Integer id, Reserva reserva) {

        Reserva reservaDb = findById(id);

        if (reservaDb == null) {
            return null;
        }

        if (reserva.getFechaReserva() != null) {
            reservaDb.setFechaReserva(reserva.getFechaReserva());
        }

        if (reserva.getDestino() != null) {
            reservaDb.setDestino(reserva.getDestino());
        }

        if (reserva.getValor() != null) {
            reservaDb.setValor(reserva.getValor());
        }

        if (reserva.getEstado() != null) {
            reservaDb.setEstado(reserva.getEstado());
        }

        if (reserva.getUsuarioId() != null) {
            reservaDb.setUsuarioId(reserva.getUsuarioId());
        }

        return reservaRepository.save(reservaDb);
    }

    // Eliminar una reserva
    @Override
    public void deleteById(Integer id) {
        reservaRepository.deleteById(id);
    }

    // Iniciar una reserva con bloqueo temporal de cupos
    @Override
    @Transactional
    public IniciarReservaResponse iniciarReserva(IniciarReservaRequest request) {

        validarSolicitud(request);

        // Liberar bloqueos vencidos antes de consultar disponibilidad.
        bloqueoCupoService.liberarBloqueosVencidos();

        // Verificar que el usuario exista.
        Usuario usuario = usuarioRepository.findById(request.getUsuarioId())
                .orElseThrow(() ->
                        new IllegalArgumentException("El usuario indicado no existe"));

        // Bloquear el vuelo durante la operación transaccional.
        Vuelo vuelo = vueloRepository.findByIdForUpdate(request.getVueloId())
                .orElseThrow(() ->
                        new IllegalArgumentException("El vuelo seleccionado no existe"));

        LocalDateTime ahora = LocalDateTime.now();

        LocalDateTime salida = vuelo.getFechaSalida()
                .atTime(vuelo.getHoraSalida());

        if (!salida.isAfter(ahora)) {
            throw new IllegalArgumentException(
                    "El vuelo seleccionado ya salió o no está disponible"
            );
        }

        int cantidadPasajeros = request.getCantidadPasajeros();

        if (vuelo.getCuposDisponibles() < cantidadPasajeros) {
            throw new IllegalArgumentException(
                    "El vuelo ya no cuenta con suficientes cupos disponibles"
            );
        }

        // Calcular el valor utilizando el precio registrado en el vuelo.
        BigDecimal valorTotal = vuelo.getPrecio()
                .multiply(BigDecimal.valueOf(cantidadPasajeros));

        LocalDateTime vencimiento = ahora.plusMinutes(MINUTOS_BLOQUEO);

        // Crear la reserva.
        Reserva reserva = new Reserva();
        reserva.setFechaReserva(LocalDate.now());
        reserva.setDestino(vuelo.getDestino());
        reserva.setValor(valorTotal);
        reserva.setEstado(ESTADO_PENDIENTE);
        reserva.setUsuarioId(usuario.getId());
        reserva.setVueloId(vuelo.getId());
        reserva.setCantidadPasajeros(cantidadPasajeros);
        reserva.setCorreoContacto(request.getCorreoContacto());
        reserva.setTelefonoContacto(request.getTelefonoContacto());

        reserva = reservaRepository.save(reserva);

        // Guardar el identificador en una variable final para utilizarlo en la lambda.
        final Integer reservaId = reserva.getId();

        // Registrar los pasajeros asociados a la reserva.
        List<Pasajero> pasajeros = request.getPasajeros()
                .stream()
                .map(datos -> {
                    Pasajero pasajero = new Pasajero();
                    pasajero.setReservaId(reservaId);
                    pasajero.setNombres(datos.getNombres().trim());
                    pasajero.setApellidos(datos.getApellidos().trim());
                    pasajero.setTipoDocumento(datos.getTipoDocumento().trim());
                    pasajero.setNumeroDocumento(datos.getNumeroDocumento().trim());
                    pasajero.setFechaNacimiento(datos.getFechaNacimiento());
                    return pasajero;
                })
                .toList();

        pasajeroRepository.saveAll(pasajeros);

        // Descontar temporalmente los cupos disponibles.
        vuelo.setCuposDisponibles(
                vuelo.getCuposDisponibles() - cantidadPasajeros
        );

        vueloRepository.save(vuelo);

        // Crear el bloqueo temporal de diez minutos.
        BloqueoCupo bloqueo = new BloqueoCupo();
        bloqueo.setReservaId(reservaId);
        bloqueo.setVueloId(vuelo.getId());
        bloqueo.setCantidadCupos(cantidadPasajeros);
        bloqueo.setFechaInicio(ahora);
        bloqueo.setFechaVencimiento(vencimiento);
        bloqueo.setEstado(ESTADO_BLOQUEO_ACTIVO);

        bloqueoCupoRepository.save(bloqueo);

        // Preparar los datos de los pasajeros para la respuesta.
        List<IniciarReservaResponse.PasajeroResponse> pasajerosRespuesta =
                pasajeros.stream()
                        .map(p -> new IniciarReservaResponse.PasajeroResponse(
                                p.getNombres(),
                                p.getApellidos(),
                                p.getTipoDocumento(),
                                p.getNumeroDocumento(),
                                p.getFechaNacimiento()
                        ))
                        .toList();

        // Retornar el resumen de la reserva.
        return new IniciarReservaResponse(
                reservaId,
                vuelo.getId(),
                vuelo.getAerolinea(),
                vuelo.getOrigen(),
                vuelo.getDestino(),
                vuelo.getFechaSalida(),
                vuelo.getHoraSalida(),
                cantidadPasajeros,
                pasajerosRespuesta,
                vuelo.getPrecio(),
                valorTotal,
                reserva.getEstado(),
                ahora,
                vencimiento
        );
    }

    // Validar los datos obligatorios de la solicitud.
    private void validarSolicitud(IniciarReservaRequest request) {

        if (request == null) {
            throw new IllegalArgumentException(
                    "La solicitud de reserva es obligatoria"
            );
        }

        if (request.getVueloId() == null || request.getVueloId() <= 0) {
            throw new IllegalArgumentException(
                    "Debe seleccionar un vuelo válido"
            );
        }

        if (request.getUsuarioId() == null || request.getUsuarioId() <= 0) {
            throw new IllegalArgumentException(
                    "Debe indicar un usuario válido"
            );
        }

        if (request.getCantidadPasajeros() == null
                || request.getCantidadPasajeros() <= 0) {
            throw new IllegalArgumentException(
                    "La cantidad de pasajeros debe ser mayor que cero"
            );
        }

        if (request.getPasajeros() == null
                || request.getPasajeros().size() != request.getCantidadPasajeros()) {
            throw new IllegalArgumentException(
                    "La cantidad de pasajeros no coincide con los datos registrados"
            );
        }

        for (IniciarReservaRequest.PasajeroRequest pasajero
                : request.getPasajeros()) {

            if (pasajero == null
                    || pasajero.getNombres() == null
                    || pasajero.getNombres().isBlank()
                    || pasajero.getApellidos() == null
                    || pasajero.getApellidos().isBlank()
                    || pasajero.getTipoDocumento() == null
                    || pasajero.getTipoDocumento().isBlank()
                    || pasajero.getNumeroDocumento() == null
                    || pasajero.getNumeroDocumento().isBlank()
                    || pasajero.getFechaNacimiento() == null) {

                throw new IllegalArgumentException(
                        "Todos los datos obligatorios de los pasajeros deben estar completos"
                );
            }

            if (pasajero.getFechaNacimiento().isAfter(LocalDate.now())) {
                throw new IllegalArgumentException(
                        "La fecha de nacimiento no puede ser futura"
                );
            }
        }
    }
}
