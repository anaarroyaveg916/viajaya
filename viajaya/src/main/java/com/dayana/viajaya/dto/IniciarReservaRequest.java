
package com.dayana.viajaya.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class IniciarReservaRequest {

    private Integer vueloId;

    private Integer usuarioId;

    private Integer cantidadPasajeros;

    private String correoContacto;

    private String telefonoContacto;

    private List<PasajeroRequest> pasajeros;

    @Data
    public static class PasajeroRequest {

        private String nombres;

        private String apellidos;

        private String tipoDocumento;

        private String numeroDocumento;

        private LocalDate fechaNacimiento;
    }
}
