package com.dayana.viajaya.services;

import com.dayana.viajaya.dtos.ConfirmacionReservaDTO;

public interface ICorreoService {
    void enviarComprobante(ConfirmacionReservaDTO confirmacion, byte[] comprobantePdf);
}
