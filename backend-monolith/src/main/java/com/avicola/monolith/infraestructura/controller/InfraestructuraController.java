package com.avicola.monolith.infraestructura.controller;

import com.avicola.monolith.infraestructura.dto.GalponDTO;
import com.avicola.monolith.infraestructura.dto.GranjaDTO;
import com.avicola.monolith.infraestructura.model.Galpon;
import com.avicola.monolith.infraestructura.model.Granja;
import com.avicola.monolith.infraestructura.service.InfraestructuraService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class InfraestructuraController {

    private final InfraestructuraService infraestructuraService;

    public InfraestructuraController(InfraestructuraService infraestructuraService) {
        this.infraestructuraService = infraestructuraService;
    }

    @GetMapping("/granjas")
    public ResponseEntity<List<Granja>> listarGranjas() {
        return ResponseEntity.ok(infraestructuraService.listarGranjas());
    }

    @PostMapping("/granjas")
    public ResponseEntity<Granja> crearGranja(@Valid @RequestBody GranjaDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(infraestructuraService.crearGranja(dto));
    }

    @GetMapping("/granjas/{granjaId}/galpones")
    public ResponseEntity<List<Galpon>> listarGalpones(@PathVariable UUID granjaId) {
        return ResponseEntity.ok(infraestructuraService.listarGalponesPorGranja(granjaId));
    }

    @PostMapping("/galpones")
    public ResponseEntity<Galpon> crearGalpon(@Valid @RequestBody GalponDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(infraestructuraService.crearGalpon(dto));
    }

    @GetMapping("/galpones/{id}")
    public ResponseEntity<Galpon> obtenerGalpon(@PathVariable UUID id) {
        return ResponseEntity.ok(infraestructuraService.obtenerGalpon(id));
    }
}
