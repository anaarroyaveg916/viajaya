package com.dayana.viajaya.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    private String token;
    private Integer usuarioId;
    private String nombre;
    private String apellido;
    private String correo;
    private String mensaje;
}