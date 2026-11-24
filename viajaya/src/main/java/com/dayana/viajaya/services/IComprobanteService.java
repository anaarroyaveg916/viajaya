package com.dayana.viajaya.services;

import com.dayana.viajaya.dto.ConfirmacionReservaDTO;

public interface IComprobanteService {
    ConfirmacionReservaDTO obtenerConfirmacion(Integer reservaId, Integer usuarioId);

    byte[] generarComprobantePdf(Integer reservaId, Integer usuarioId);

    String enviarComprobantePorCorreo(Integer reservaId, Integer usuarioId);
}
