
package com.dayana.viajaya.repositories;

import com.dayana.viajaya.entities.BloqueoCupo;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface IBloqueoCupoRepository extends JpaRepository<BloqueoCupo, Integer> {

    Optional<BloqueoCupo> findByReservaId(Integer reservaId);

    List<BloqueoCupo> findByEstado(String estado);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            SELECT b FROM BloqueoCupo b
            WHERE b.estado = :estado
            AND b.fechaVencimiento <= :ahora
            """)
    List<BloqueoCupo> findVencidosForUpdate(
            @Param("estado") String estado,
            @Param("ahora") LocalDateTime ahora
    );
}
