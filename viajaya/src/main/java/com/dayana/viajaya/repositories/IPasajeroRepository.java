
package com.dayana.viajaya.repositories;

import com.dayana.viajaya.entities.Pasajero;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IPasajeroRepository extends JpaRepository<Pasajero, Integer> {

    List<Pasajero> findByReservaId(Integer reservaId);
}
