
package com.dayana.viajaya.services.impl;

import com.dayana.viajaya.entities.BloqueoCupo;
import com.dayana.viajaya.entities.Reserva;
import com.dayana.viajaya.entities.Vuelo;
import com.dayana.viajaya.repositories.IBloqueoCupoRepository;
import com.dayana.viajaya.repositories.IReservaRepository;
import com.dayana.viajaya.repositories.IVueloRepository;
import com.dayana.viajaya.services.IBloqueoCupoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BloqueoCupoServiceImpl implements IBloqueoCupoService {

    private static final String ESTADO_ACTIVO = "ACTIVO";
    private static final String ESTADO_VENCIDO = "VENCIDO";
    private static final String ESTADO_PENDIENTE = "PENDIENTE";
    private static final String ESTADO_EXPIRADA = "EXPIRADA";

    private final IBloqueoCupoRepository bloqueoCupoRepository;
    private final IVueloRepository vueloRepository;
    private final IReservaRepository reservaRepository;

    @Override
    @Transactional
    public void liberarBloqueosVencidos() {

        LocalDateTime ahora = LocalDateTime.now();

        List<BloqueoCupo> bloqueos =
                bloqueoCupoRepository.findVencidosForUpdate(ESTADO_ACTIVO, ahora);

        for (BloqueoCupo bloqueo : bloqueos) {

            Vuelo vuelo = vueloRepository.findByIdForUpdate(bloqueo.getVueloId())
                    .orElseThrow(() ->
                            new IllegalStateException(
                                    "No existe el vuelo asociado al bloqueo: "
                                            + bloqueo.getVueloId()
                            )
                    );

            Reserva reserva = reservaRepository.findById(bloqueo.getReservaId())
                    .orElseThrow(() ->
                            new IllegalStateException(
                                    "No existe la reserva asociada al bloqueo: "
                                            + bloqueo.getReservaId()
                            )
                    );

            int cuposActuales = vuelo.getCuposDisponibles();
            int cuposPorDevolver = bloqueo.getCantidadCupos();
            int capacidadTotal = vuelo.getCuposTotales();

            int nuevaDisponibilidad = Math.min(
                    capacidadTotal,
                    cuposActuales + cuposPorDevolver
            );

            vuelo.setCuposDisponibles(nuevaDisponibilidad);
            bloqueo.setEstado(ESTADO_VENCIDO);

            if (ESTADO_PENDIENTE.equals(reserva.getEstado())) {
                reserva.setEstado(ESTADO_EXPIRADA);
            }

            vueloRepository.save(vuelo);
            bloqueoCupoRepository.save(bloqueo);
            reservaRepository.save(reserva);
        }
    }
}
