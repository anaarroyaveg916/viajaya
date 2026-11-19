package com.dayana.viajaya.controllers;

import com.dayana.viajaya.entities.Reserva;
import com.dayana.viajaya.services.IReservaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reservas")
public class ReservaController {
    private final IReservaService reservaService;

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