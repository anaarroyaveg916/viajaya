
package com.dayana.viajaya.repositories;

import com.dayana.viajaya.entities.Vuelo;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface IVueloRepository extends JpaRepository<Vuelo, Integer> {

    List<Vuelo> findByOrigenIgnoreCaseAndDestinoIgnoreCaseAndFechaSalidaAndCuposDisponiblesGreaterThanEqual(
            String origen,
            String destino,
            LocalDate fechaSalida,
            Integer pasajeros
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT v FROM Vuelo v WHERE v.id = :id")
    Optional<Vuelo> findByIdForUpdate(@Param("id") Integer id);
}
