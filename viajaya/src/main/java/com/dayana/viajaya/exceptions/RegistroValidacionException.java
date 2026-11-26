package com.dayana.viajaya.exceptions;

import lombok.Getter;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

@Getter
public class RegistroValidacionException extends RuntimeException {

    private final Map<String, String> erroresPorCampo;

    public RegistroValidacionException(Map<String, String> erroresPorCampo) {
        super("Error de validación en el registro de usuario");
        this.erroresPorCampo = erroresPorCampo != null
                ? Collections.unmodifiableMap(new LinkedHashMap<>(erroresPorCampo))
                : Collections.emptyMap();
    }
}