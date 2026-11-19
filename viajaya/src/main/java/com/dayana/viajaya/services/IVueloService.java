package com.dayana.viajaya.services;

import com.dayana.viajaya.entities.Vuelo;
import java.time.LocalDate;
import java.util.List;

public interface IVueloService {
    List<Vuelo> findAll();
    Vuelo findById(Integer id);
    List<Vuelo> buscar(String origen, String destino, LocalDate fechaSalida, Integer pasajeros);
    Vuelo save(Vuelo vuelo);
    Vuelo update(Integer id, Vuelo vuelo);
    void deleteById(Integer id);
}
