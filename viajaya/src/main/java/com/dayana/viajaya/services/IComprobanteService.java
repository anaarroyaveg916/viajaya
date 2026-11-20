package com.dayana.viajaya.services;

public interface IComprobanteService {
    byte[] generarComprobantePdf(Integer reservaId, Integer usuarioId);
}
