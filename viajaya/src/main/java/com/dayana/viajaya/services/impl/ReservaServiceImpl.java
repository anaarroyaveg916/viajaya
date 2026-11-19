package com.dayana.viajaya.services.impl;

import com.dayana.viajaya.entities.Reserva;
import com.dayana.viajaya.repositories.IReservaRepository;
import com.dayana.viajaya.services.IReservaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservaServiceImpl implements IReservaService {
    private final IReservaRepository reservaRepository;

    //public ReservaServiceImpl(IReservaRepository reservaRepository) {
      //  this.reservaRepository = reservaRepository;
    //}

    @Override
    public List<Reserva> findAll() {
        return reservaRepository.findAll();
    }

    @Override
    public Reserva findById(Integer id) {
        return reservaRepository.findById(id).orElse(null);
    }

    @Override
    public List<Reserva> findByUsuarioId(Integer usuarioId) {
        return reservaRepository.findByUsuarioId(usuarioId);
    }

    @Override
    public Reserva save(Reserva reserva) {
        return reservaRepository.save(reserva);
    }

    @Override
    public Reserva update(Integer id, Reserva reserva) {
        Reserva reservaDb = findById(id);
        if (reservaDb == null) return null;

        reservaDb.setFechaReserva(reserva.getFechaReserva());
        reservaDb.setDestino(reserva.getDestino());
        reservaDb.setValor(reserva.getValor());
        reservaDb.setEstado(reserva.getEstado());
        reservaDb.setUsuarioId(reserva.getUsuarioId());
        return reservaRepository.save(reservaDb);
    }

    @Override
    public Reserva patch(Integer id, Reserva reserva) {
        Reserva reservaDb = findById(id);
        if (reservaDb == null) return null;

        if (reserva.getFechaReserva() != null)
            reservaDb.setFechaReserva(reserva.getFechaReserva());
        if (reserva.getDestino() != null)
            reservaDb.setDestino(reserva.getDestino());
        if (reserva.getValor() != null)
            reservaDb.setValor(reserva.getValor());
        if (reserva.getEstado() != null)
            reservaDb.setEstado(reserva.getEstado());
        if (reserva.getUsuarioId() != null)
            reservaDb.setUsuarioId(reserva.getUsuarioId());

        return reservaRepository.save(reservaDb);
    }

    @Override
    public void deleteById(Integer id) {
        reservaRepository.deleteById(id);
    }
}