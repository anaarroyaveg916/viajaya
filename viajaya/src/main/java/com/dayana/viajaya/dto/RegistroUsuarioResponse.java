package com.dayana.viajaya.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistroUsuarioResponse {
    private Integer id;
    private String nombre;
    private String apellido;
    private String correo;
    private String telefono;
    private String mensaje;
}