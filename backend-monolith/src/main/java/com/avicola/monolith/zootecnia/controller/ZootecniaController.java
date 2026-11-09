package com.avicola.monolith.zootecnia.controller;

import com.avicola.monolith.zootecnia.dto.ReporteZootecnicoDTO;
import com.avicola.monolith.zootecnia.service.CalculoZootecnicoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/zootecnia")
public class ZootecniaController {

    private final CalculoZootecnicoService zootecnicoService;

    public ZootecniaController(CalculoZootecnicoService zootecnicoService) {
        this.zootecnicoService = zootecnicoService;
    }

    @GetMapping("/lote/{loteId}/reporte")
    public ResponseEntity<ReporteZootecnicoDTO> obtenerReportePorLote(@PathVariable UUID loteId) {
        return ResponseEntity.ok(zootecnicoService.generarReporteZootecnico(loteId));
    }
}
