package com.dayana.viajaya.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "vuelos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Vuelo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "aerolinea", nullable = false, length = 100)
    private String aerolinea;

    @Column(name = "origen", nullable = false, length = 100)
    private String origen;

    @Column(name = "destino", nullable = false, length = 100)
    private String destino;

    @Column(name = "fecha_salida", nullable = false)
    private LocalDate fechaSalida;

    @Column(name = "hora_salida", nullable = false)
    private LocalTime horaSalida;

    @Column(name = "fecha_llegada")
    private LocalDate fechaLlegada;

    @Column(name = "hora_llegada")
    private LocalTime horaLlegada;

    @Column(name = "duracion_minutos")
    private Integer duracionMinutos;

    @Column(name = "precio", nullable = false, precision = 12, scale = 2)
    private BigDecimal precio;

    @Column(name = "escalas", nullable = false)
    private Integer escalas;

    @Column(name = "equipaje", length = 255)
    private String equipaje;

    @Column(name = "cupos_disponibles", nullable = false)
    private Integer cuposDisponibles;

    @Column(name = "cupos_totales", nullable = false)
    private Integer cuposTotales;
}
