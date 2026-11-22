package com.dayana.viajaya.services;

import com.dayana.viajaya.dtos.ConfirmacionReservaDTO;

public interface IComprobanteService {
    ConfirmacionReservaDTO obtenerConfirmacion(Integer reservaId, Integer usuarioId);

    byte[] generarComprobantePdf(Integer reservaId, Integer usuarioId);
}
