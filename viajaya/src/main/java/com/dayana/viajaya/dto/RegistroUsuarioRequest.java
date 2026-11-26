package com.dayana.viajaya.dto;

import lombok.Data;

@Data
public class RegistroUsuarioRequest {
    private String nombre;
    private String apellido;
    private String correo;
    private String telefono;
    private String password;
}