
package com.dayana.viajaya.services;

import com.dayana.viajaya.dto.IniciarReservaRequest;
import com.dayana.viajaya.dto.IniciarReservaResponse;
import com.dayana.viajaya.entities.Reserva;

import java.util.List;

public interface IReservaService {

    List<Reserva> findAll();

    Reserva findById(Integer id);

    List<Reserva> findByUsuarioId(Integer usuarioId);

    Reserva save(Reserva reserva);

    Reserva update(Integer id, Reserva reserva);

    Reserva patch(Integer id, Reserva reserva);

    void deleteById(Integer id);

    IniciarReservaResponse iniciarReserva(IniciarReservaRequest request);
}
