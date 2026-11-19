package com.dayana.viajaya.repositories;

import com.dayana.viajaya.entities.Vuelo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface IVueloRepository extends JpaRepository<Vuelo, Integer> {
    List<Vuelo> findByOrigenIgnoreCaseAndDestinoIgnoreCaseAndFechaSalidaAndCuposDisponiblesGreaterThanEqual(
            String origen,
            String destino,
            LocalDate fechaSalida,
            Integer pasajeros
    );
}
