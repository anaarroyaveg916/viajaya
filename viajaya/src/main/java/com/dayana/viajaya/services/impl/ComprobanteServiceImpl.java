package com.dayana.viajaya.services.impl;

import com.dayana.viajaya.dto.ConfirmacionReservaDTO;
import com.dayana.viajaya.entities.Reserva;
import com.dayana.viajaya.entities.Usuario;
import com.dayana.viajaya.repositories.IReservaRepository;
import com.dayana.viajaya.repositories.IUsuarioRepository;
import com.dayana.viajaya.services.IComprobanteService;
import com.dayana.viajaya.services.ICorreoService;
import lombok.RequiredArgsConstructor;
import org.openpdf.text.Document;
import org.openpdf.text.DocumentException;
import org.openpdf.text.Element;
import org.openpdf.text.Font;
import org.openpdf.text.FontFactory;
import org.openpdf.text.PageSize;
import org.openpdf.text.Paragraph;
import org.openpdf.text.Phrase;
import org.openpdf.text.Rectangle;
import org.openpdf.text.pdf.PdfPCell;
import org.openpdf.text.pdf.PdfPTable;
import org.openpdf.text.pdf.PdfWriter;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class ComprobanteServiceImpl implements IComprobanteService {
    private final IReservaRepository reservaRepository;
    private final IUsuarioRepository usuarioRepository;
    private final ICorreoService correoService;

    private static final String SERVICIO_RESERVA = "Reserva de viaje";

    private static final Color COLOR_BORDE = new Color(200, 200, 200);

    private static final Font FUENTE_MARCA = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 24, Color.BLACK);
    private static final Font FUENTE_SUBTITULO = FontFactory.getFont(FontFactory.HELVETICA, 12, Color.BLACK);
    private static final Font FUENTE_SECCION = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Color.BLACK);
    private static final Font FUENTE_NEGRITA = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Color.BLACK);
    private static final Font FUENTE_NORMAL = FontFactory.getFont(FontFactory.HELVETICA, 10, Color.BLACK);
    private static final Font FUENTE_NOTA = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 8, Color.BLACK);

    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter FORMATO_FECHA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final Locale LOCALE_COLOMBIA = Locale.of("es", "CO");

    @Override
    public ConfirmacionReservaDTO obtenerConfirmacion(Integer reservaId, Integer usuarioId) {
        Reserva reserva = reservaRepository.findById(reservaId).orElse(null);
        // Si la reserva no existe o no pertenece al usuario se responde igual,
        // para no revelar la existencia ni los datos de reservas ajenas.
        if (reserva == null || !reserva.getUsuarioId().equals(usuarioId)) {
            return null;
        }
        Usuario titular = usuarioRepository.findById(reserva.getUsuarioId()).orElse(null);
        if (titular == null) {
            return null;
        }
        return new ConfirmacionReservaDTO(
                reserva.getId(),
                generarCodigo(reserva),
                SERVICIO_RESERVA,
                reserva.getDestino(),
                reserva.getFechaReserva(),
                reserva.getValor(),
                reserva.getEstado(),
                titular
        );
    }

    @Override
    public byte[] generarComprobantePdf(Integer reservaId, Integer usuarioId) {
        ConfirmacionReservaDTO confirmacion = obtenerConfirmacion(reservaId, usuarioId);
        if (confirmacion == null) {
            return null;
        }
        return construirPdf(confirmacion);
    }

    @Override
    public String enviarComprobantePorCorreo(Integer reservaId, Integer usuarioId) {
        ConfirmacionReservaDTO confirmacion = obtenerConfirmacion(reservaId, usuarioId);
        if (confirmacion == null) {
            return null;
        }
        // Solo se envía al correo registrado del titular, nunca a una dirección recibida en la petición.
        correoService.enviarComprobante(confirmacion, construirPdf(confirmacion));
        return confirmacion.getTitular().getCorreo();
    }

    private byte[] construirPdf(ConfirmacionReservaDTO confirmacion) {
        ByteArrayOutputStream salida = new ByteArrayOutputStream();
        try (Document documento = new Document(PageSize.A4, 50, 50, 50, 50)) {
            PdfWriter.getInstance(documento, salida);
            documento.open();
            agregarEncabezado(documento, confirmacion);
            agregarDatosTitular(documento, confirmacion.getTitular());
            agregarDetalleServicio(documento, confirmacion);
            agregarPiePagina(documento);
        } catch (DocumentException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "No fue posible generar el comprobante de la reserva");
        }
        return salida.toByteArray();
    }

    private String generarCodigo(Reserva reserva) {
        return String.format("VJY-%06d", reserva.getId());
    }

    private void agregarEncabezado(Document documento, ConfirmacionReservaDTO confirmacion) {
        PdfPTable encabezado = new PdfPTable(new float[]{4, 1, 2});
        encabezado.setWidthPercentage(100);
        encabezado.setSpacingAfter(25);

        encabezado.addCell(crearCeldaEncabezado("ViajaYa", FUENTE_MARCA));
        encabezado.addCell(crearCeldaEncabezado("Código", FUENTE_NORMAL));
        encabezado.addCell(crearCeldaEncabezado(confirmacion.getCodigo(), FUENTE_NEGRITA));

        encabezado.addCell(crearCeldaEncabezado("Comprobante de reserva", FUENTE_SUBTITULO));
        encabezado.addCell(crearCeldaEncabezado("Estado", FUENTE_NORMAL));
        encabezado.addCell(crearCeldaEncabezado(confirmacion.getEstado().toUpperCase(LOCALE_COLOMBIA), FUENTE_NEGRITA));

        documento.add(encabezado);
    }

    private void agregarDatosTitular(Document documento, Usuario titular) {
        documento.add(crearTituloSeccion("Datos del titular"));
        PdfPTable tabla = crearTablaDetalle();
        agregarFila(tabla, "Nombre", titular.getNombre() + " " + titular.getApellido());
        agregarFila(tabla, "Correo", titular.getCorreo());
        agregarFila(tabla, "Teléfono", titular.getTelefono() != null ? titular.getTelefono() : "No registrado");
        documento.add(tabla);
    }

    private void agregarDetalleServicio(Document documento, ConfirmacionReservaDTO confirmacion) {
        NumberFormat formatoMoneda = NumberFormat.getCurrencyInstance(LOCALE_COLOMBIA);
        documento.add(crearTituloSeccion("Detalle del servicio"));
        PdfPTable tabla = crearTablaDetalle();
        agregarFila(tabla, "Servicio", confirmacion.getServicio());
        agregarFila(tabla, "Destino", confirmacion.getDestino());
        agregarFila(tabla, "Fecha de reserva", confirmacion.getFechaReserva().format(FORMATO_FECHA));
        agregarFila(tabla, "Valor total", formatoMoneda.format(confirmacion.getValor()) + " COP");
        documento.add(tabla);
    }

    private void agregarPiePagina(Document documento) {
        Paragraph emision = new Paragraph("Fecha de emisión: " + LocalDateTime.now().format(FORMATO_FECHA_HORA), FUENTE_NOTA);
        emision.setSpacingBefore(30);
        documento.add(emision);
        documento.add(new Paragraph(
                "Este documento es la evidencia de su reserva en ViajaYa. Consérvelo para cualquier consulta o seguimiento.",
                FUENTE_NOTA));
    }

    private PdfPCell crearCeldaEncabezado(String texto, Font fuente) {
        PdfPCell celda = new PdfPCell(new Phrase(texto, fuente));
        celda.setBorder(Rectangle.NO_BORDER);
        celda.setVerticalAlignment(Element.ALIGN_BOTTOM);
        celda.setPadding(2);
        return celda;
    }

    private Paragraph crearTituloSeccion(String titulo) {
        Paragraph parrafo = new Paragraph(titulo, FUENTE_SECCION);
        parrafo.setSpacingAfter(6);
        return parrafo;
    }

    private PdfPTable crearTablaDetalle() {
        PdfPTable tabla = new PdfPTable(new float[]{1, 2});
        tabla.setWidthPercentage(100);
        tabla.setSpacingAfter(18);
        return tabla;
    }

    private void agregarFila(PdfPTable tabla, String etiqueta, String valor) {
        PdfPCell celdaEtiqueta = new PdfPCell(new Phrase(etiqueta, FUENTE_NEGRITA));
        celdaEtiqueta.setBorder(Rectangle.BOTTOM);
        celdaEtiqueta.setBorderColor(COLOR_BORDE);
        celdaEtiqueta.setPadding(7);
        tabla.addCell(celdaEtiqueta);

        PdfPCell celdaValor = new PdfPCell(new Phrase(valor, FUENTE_NORMAL));
        celdaValor.setBorder(Rectangle.BOTTOM);
        celdaValor.setBorderColor(COLOR_BORDE);
        celdaValor.setPadding(7);
        celdaValor.setHorizontalAlignment(Element.ALIGN_LEFT);
        tabla.addCell(celdaValor);
    }
}
