package com.dayana.viajaya.controllers;

import com.dayana.viajaya.entities.Vuelo;
import com.dayana.viajaya.services.IVueloService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/vuelos")
public class VueloController {
    private final IVueloService vueloService;

    public VueloController(IVueloService vueloService) {
        this.vueloService = vueloService;
    }

    @GetMapping
    public List<Vuelo> findAll() {
        return vueloService.findAll();
    }

    /**
     * Extensión PVY-9: búsqueda con validaciones de negocio.
     * La guía base Usuario/Reserva no incluía este endpoint.
     */
    @GetMapping("/buscar")
    public ResponseEntity<?> buscar(
            @RequestParam(required = false) String origen,
            @RequestParam(required = false) String destino,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaSalida,
            @RequestParam(required = false) Integer pasajeros) {
        List<Vuelo> resultados = vueloService.buscar(origen, destino, fechaSalida, pasajeros);
        if (resultados.isEmpty()) {
            Map<String, String> respuesta = new HashMap<>();
            respuesta.put("mensaje", "No hay resultados para los criterios indicados. Modifique origen, destino, fecha o pasajeros.");
            return ResponseEntity.ok(respuesta);
        }
        return ResponseEntity.ok(resultados);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Vuelo> findById(@PathVariable Integer id) {
        Vuelo vuelo = vueloService.findById(id);
        if (vuelo == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(vuelo);
    }

    @PostMapping
    public ResponseEntity<Vuelo> save(@RequestBody Vuelo vuelo) {
        return ResponseEntity.status(HttpStatus.CREATED).body(vueloService.save(vuelo));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Vuelo> update(
            @PathVariable Integer id, @RequestBody Vuelo vuelo) {
        Vuelo actualizado = vueloService.update(id, vuelo);
        if (actualizado == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(actualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Integer id) {
        if (vueloService.findById(id) == null) return ResponseEntity.notFound().build();
        vueloService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
