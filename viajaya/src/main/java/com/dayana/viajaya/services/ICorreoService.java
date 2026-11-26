package com.dayana.viajaya.services;

import com.dayana.viajaya.dto.ConfirmacionReservaDTO;

public interface ICorreoService {
    void enviarComprobante(ConfirmacionReservaDTO confirmacion, byte[] comprobantePdf);
}
