package com.dayana.viajaya.dtos;

import com.dayana.viajaya.entities.Usuario;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConfirmacionReservaDTO {

    private Integer reservaId;

    private String codigo;

    private String servicio;

    private String destino;

    private LocalDate fechaReserva;

    private BigDecimal valor;

    private String estado;

    private Usuario titular;
}
