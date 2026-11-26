
package com.dayana.viajaya.controllers;

import com.dayana.viajaya.dto.ConfirmacionReservaDTO;
import com.dayana.viajaya.dto.IniciarReservaRequest;
import com.dayana.viajaya.dto.IniciarReservaResponse;
import com.dayana.viajaya.entities.Reserva;
import com.dayana.viajaya.services.IComprobanteService;
import com.dayana.viajaya.services.IReservaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reservas")
public class ReservaController {

    private final IReservaService reservaService;
    private final IComprobanteService comprobanteService;

    // Consultar todas las reservas
    @GetMapping
    public List<Reserva> findAll() {
        return reservaService.findAll();
    }

    // Consultar una reserva por identificador
    @GetMapping("/{id}")
    public ResponseEntity<Reserva> findById(@PathVariable Integer id) {

        Reserva reserva = reservaService.findById(id);

        if (reserva == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(reserva);
    }

    // Consultar reservas de un usuario
    @GetMapping("/usuario/{usuarioId}")
    public List<Reserva> findByUsuarioId(@PathVariable Integer usuarioId) {
        return reservaService.findByUsuarioId(usuarioId);
    }

    // Consultar la confirmación de una reserva del usuario
    @GetMapping("/{id}/confirmacion")
    public ResponseEntity<ConfirmacionReservaDTO> obtenerConfirmacion(
            @PathVariable Integer id,
            @RequestParam Integer usuarioId) {

        ConfirmacionReservaDTO confirmacion =
                comprobanteService.obtenerConfirmacion(id, usuarioId);

        if (confirmacion == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(confirmacion);
    }

    // Descargar el comprobante de una reserva en PDF
    @GetMapping("/{id}/comprobante")
    public ResponseEntity<byte[]> generarComprobante(
            @PathVariable Integer id,
            @RequestParam Integer usuarioId) {

        byte[] comprobante =
                comprobanteService.generarComprobantePdf(id, usuarioId);

        if (comprobante == null) {
            return ResponseEntity.notFound().build();
        }

        ContentDisposition adjunto = ContentDisposition.attachment()
                .filename("comprobante-reserva-" + id + ".pdf")
                .build();

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, adjunto.toString())
                .body(comprobante);
    }

    // Enviar el comprobante de una reserva al correo del titular
    @PostMapping("/{id}/comprobante/correo")
    public ResponseEntity<Map<String, String>> enviarComprobantePorCorreo(
            @PathVariable Integer id,
            @RequestParam Integer usuarioId) {

        String correo =
                comprobanteService.enviarComprobantePorCorreo(id, usuarioId);

        if (correo == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(
                Map.of("mensaje", "El comprobante fue enviado al correo " + correo)
        );
    }

    // Crear una reserva mediante el CRUD existente
    @PostMapping
    public ResponseEntity<Reserva> save(@RequestBody Reserva reserva) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(reservaService.save(reserva));
    }

    // Iniciar una reserva con bloqueo temporal de cupos
    @PostMapping("/iniciar")
    public ResponseEntity<IniciarReservaResponse> iniciarReserva(
            @RequestBody IniciarReservaRequest request) {

        IniciarReservaResponse respuesta =
                reservaService.iniciarReserva(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
    }

    // Actualizar una reserva
    @PutMapping("/{id}")
    public ResponseEntity<Reserva> update(
            @PathVariable Integer id,
            @RequestBody Reserva reserva) {

        Reserva actualizada = reservaService.update(id, reserva);

        if (actualizada == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(actualizada);
    }

    // Actualización parcial de una reserva
    @PatchMapping("/{id}")
    public ResponseEntity<Reserva> patch(
            @PathVariable Integer id,
            @RequestBody Reserva reserva) {

        Reserva actualizada = reservaService.patch(id, reserva);

        if (actualizada == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(actualizada);
    }

    // Eliminar una reserva
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Integer id) {

        if (reservaService.findById(id) == null) {
            return ResponseEntity.notFound().build();
        }

        reservaService.deleteById(id);

        return ResponseEntity.noContent().build();
    }

    // Manejar errores de validación del proceso de reserva
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> manejarErrorValidacion(
            IllegalArgumentException exception) {

        return ResponseEntity.badRequest().body(
                Map.of("error", exception.getMessage())
        );
    }
}
