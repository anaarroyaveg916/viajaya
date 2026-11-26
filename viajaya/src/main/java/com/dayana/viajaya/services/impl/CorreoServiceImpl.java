package com.dayana.viajaya.services.impl;

import com.dayana.viajaya.dto.ConfirmacionReservaDTO;
import com.dayana.viajaya.services.ICorreoService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class CorreoServiceImpl implements ICorreoService {
    private final JavaMailSender mailSender;

    @Value("${viajaya.mail.remitente}")
    private String remitente;

    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Override
    public void enviarComprobante(ConfirmacionReservaDTO confirmacion, byte[] comprobantePdf) {
        try {
            MimeMessage mensaje = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mensaje, true, "UTF-8");
            helper.setFrom(remitente);
            helper.setTo(confirmacion.getTitular().getCorreo());
            helper.setSubject("Comprobante de su reserva " + confirmacion.getCodigo() + " - ViajaYa");
            helper.setText(construirCuerpo(confirmacion));
            helper.addAttachment(
                    "comprobante-reserva-" + confirmacion.getReservaId() + ".pdf",
                    new ByteArrayResource(comprobantePdf),
                    "application/pdf");
            mailSender.send(mensaje);
        } catch (MessagingException | MailException e) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "No fue posible enviar el comprobante por correo", e);
        }
    }

    private String construirCuerpo(ConfirmacionReservaDTO confirmacion) {
        return """
                Hola %s,

                Adjuntamos el comprobante de su reserva en ViajaYa.

                Código: %s
                Servicio: %s
                Destino: %s
                Fecha de reserva: %s
                Estado: %s

                Conserve este correo como evidencia de su reserva.

                ViajaYa
                """.formatted(
                confirmacion.getTitular().getNombre(),
                confirmacion.getCodigo(),
                confirmacion.getServicio(),
                confirmacion.getDestino(),
                confirmacion.getFechaReserva().format(FORMATO_FECHA),
                confirmacion.getEstado());
    }
}
