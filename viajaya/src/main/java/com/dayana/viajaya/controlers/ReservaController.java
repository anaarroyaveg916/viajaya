package com.dayana.viajaya.controllers;

import com.dayana.viajaya.dtos.ConfirmacionReservaDTO;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reservas")
public class ReservaController {
    private final IReservaService reservaService;
    private final IComprobanteService comprobanteService;

    //public ReservaController(IReservaService reservaService) {
      //  this.reservaService = reservaService;
    //}

    @GetMapping
    public List<Reserva> findAll() {
        return reservaService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Reserva> findById(@PathVariable Integer id) {
        Reserva reserva = reservaService.findById(id);
        if (reserva == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(reserva);
    }

    @GetMapping("/usuario/{usuarioId}")
    public List<Reserva> findByUsuarioId(@PathVariable Integer usuarioId) {
        return reservaService.findByUsuarioId(usuarioId);
    }

    @GetMapping("/{id}/confirmacion")
    public ResponseEntity<ConfirmacionReservaDTO> obtenerConfirmacion(
            @PathVariable Integer id, @RequestParam Integer usuarioId) {
        ConfirmacionReservaDTO confirmacion = comprobanteService.obtenerConfirmacion(id, usuarioId);
        if (confirmacion == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(confirmacion);
    }

    @GetMapping("/{id}/comprobante")
    public ResponseEntity<byte[]> generarComprobante(
            @PathVariable Integer id, @RequestParam Integer usuarioId) {
        byte[] comprobante = comprobanteService.generarComprobantePdf(id, usuarioId);
        if (comprobante == null) return ResponseEntity.notFound().build();
        ContentDisposition adjunto = ContentDisposition.attachment()
                .filename("comprobante-reserva-" + id + ".pdf")
                .build();
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, adjunto.toString())
                .body(comprobante);
    }

    @PostMapping("/{id}/comprobante/correo")
    public ResponseEntity<Map<String, String>> enviarComprobantePorCorreo(
            @PathVariable Integer id, @RequestParam Integer usuarioId) {
        String correo = comprobanteService.enviarComprobantePorCorreo(id, usuarioId);
        if (correo == null) return ResponseEntity.notFound().build();
        Map<String, String> respuesta = new HashMap<>();
        respuesta.put("mensaje", "El comprobante fue enviado al correo " + correo);
        return ResponseEntity.ok(respuesta);
    }

    @PostMapping
    public ResponseEntity<Reserva> save(@RequestBody Reserva reserva) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reservaService.save(reserva));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Reserva> update(
            @PathVariable Integer id, @RequestBody Reserva reserva) {
        Reserva actualizada = reservaService.update(id, reserva);
        if (actualizada == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(actualizada);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Reserva> patch(
            @PathVariable Integer id, @RequestBody Reserva reserva) {
        Reserva actualizada = reservaService.patch(id, reserva);
        if (actualizada == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(actualizada);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Integer id) {
        if (reservaService.findById(id) == null) return ResponseEntity.notFound().build();
        reservaService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}