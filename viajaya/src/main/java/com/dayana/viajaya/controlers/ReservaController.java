
package com.dayana.viajaya.controllers;

import com.dayana.viajaya.dto.IniciarReservaRequest;
import com.dayana.viajaya.dto.IniciarReservaResponse;
import com.dayana.viajaya.entities.Reserva;
import com.dayana.viajaya.services.IReservaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reservas")
public class ReservaController {

    private final IReservaService reservaService;

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
