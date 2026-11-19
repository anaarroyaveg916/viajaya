package com.dayana.viajaya.services.impl;

import com.dayana.viajaya.entities.Vuelo;
import com.dayana.viajaya.repositories.IVueloRepository;
import com.dayana.viajaya.services.IVueloService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDate;
import java.util.List;

@Service
public class VueloServiceImpl implements IVueloService {
    private final IVueloRepository vueloRepository;

    public VueloServiceImpl(IVueloRepository vueloRepository) {
        this.vueloRepository = vueloRepository;
    }

    @Override
    public List<Vuelo> findAll() {
        return vueloRepository.findAll();
    }

    @Override
    public Vuelo findById(Integer id) {
        return vueloRepository.findById(id).orElse(null);
    }

    @Override
    public List<Vuelo> buscar(String origen, String destino, LocalDate fechaSalida, Integer pasajeros) {
        if (origen == null || origen.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El origen es obligatorio");
        }
        if (destino == null || destino.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El destino es obligatorio");
        }
        if (origen.trim().equalsIgnoreCase(destino.trim())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El origen y el destino deben ser diferentes");
        }
        if (fechaSalida == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La fecha de salida es obligatoria");
        }
        if (fechaSalida.isBefore(LocalDate.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La fecha de salida no puede ser anterior a la fecha actual");
        }
        if (pasajeros == null || pasajeros < 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El número de pasajeros debe ser un entero mayor que cero");
        }

        return vueloRepository
                .findByOrigenIgnoreCaseAndDestinoIgnoreCaseAndFechaSalidaAndCuposDisponiblesGreaterThanEqual(
                        origen.trim(),
                        destino.trim(),
                        fechaSalida,
                        pasajeros
                );
    }

    @Override
    public Vuelo save(Vuelo vuelo) {
        return vueloRepository.save(vuelo);
    }

    @Override
    public Vuelo update(Integer id, Vuelo vuelo) {
        Vuelo vueloDb = findById(id);
        if (vueloDb == null) return null;

        vueloDb.setAerolinea(vuelo.getAerolinea());
        vueloDb.setOrigen(vuelo.getOrigen());
        vueloDb.setDestino(vuelo.getDestino());
        vueloDb.setFechaSalida(vuelo.getFechaSalida());
        vueloDb.setHoraSalida(vuelo.getHoraSalida());
        vueloDb.setFechaLlegada(vuelo.getFechaLlegada());
        vueloDb.setHoraLlegada(vuelo.getHoraLlegada());
        vueloDb.setDuracionMinutos(vuelo.getDuracionMinutos());
        vueloDb.setPrecio(vuelo.getPrecio());
        vueloDb.setEscalas(vuelo.getEscalas());
        vueloDb.setEquipaje(vuelo.getEquipaje());
        vueloDb.setCuposDisponibles(vuelo.getCuposDisponibles());
        return vueloRepository.save(vueloDb);
    }

    @Override
    public void deleteById(Integer id) {
        vueloRepository.deleteById(id);
    }
}
