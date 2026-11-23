
package com.dayana.viajaya.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IniciarReservaResponse {

    private Integer reservaId;

    private Integer vueloId;

    private String aerolinea;

    private String origen;

    private String destino;

    private LocalDate fechaSalida;

    private LocalTime horaSalida;

    private Integer cantidadPasajeros;

    private List<PasajeroResponse> pasajeros;

    private BigDecimal precioPorPasajero;

    private BigDecimal valorTotal;

    private String estadoReserva;

    private LocalDateTime fechaInicioBloqueo;

    private LocalDateTime fechaVencimientoBloqueo;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PasajeroResponse {

        private String nombres;

        private String apellidos;

        private String tipoDocumento;

        private String numeroDocumento;

        private LocalDate fechaNacimiento;
    }
}
