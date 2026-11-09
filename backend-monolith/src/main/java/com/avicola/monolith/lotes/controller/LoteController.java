package com.avicola.monolith.lotes.controller;

import com.avicola.monolith.lotes.dto.LoteResponseDTO;
import com.avicola.monolith.lotes.dto.NuevoLoteDTO;
import com.avicola.monolith.lotes.service.LoteService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class LoteController {

    private final LoteService loteService;

    public LoteController(LoteService loteService) {
        this.loteService = loteService;
    }

    @GetMapping("/galpones/{galponId}/lotes")
    public ResponseEntity<List<LoteResponseDTO>> listarLotesPorGalpon(@PathVariable UUID galponId,
                                                                      @RequestParam(defaultValue = "true") boolean soloActivos) {
        if (soloActivos) {
            return ResponseEntity.ok(loteService.listarLotesActivosPorGalpon(galponId));
        }
        return ResponseEntity.ok(loteService.listarLotesPorGalpon(galponId));
    }

    @PostMapping("/galpones/{galponId}/lotes")
    public ResponseEntity<LoteResponseDTO> crearLote(@PathVariable UUID galponId,
                                                     @Valid @RequestBody NuevoLoteDTO dto) {
        dto.setGalponId(galponId);
        return ResponseEntity.status(HttpStatus.CREATED).body(loteService.crearLote(dto));
    }

    @PatchMapping("/lotes/{loteId}/cerrar")
    public ResponseEntity<LoteResponseDTO> cerrarLote(@PathVariable UUID loteId) {
        return ResponseEntity.ok(loteService.cerrarLote(loteId));
    }
}
