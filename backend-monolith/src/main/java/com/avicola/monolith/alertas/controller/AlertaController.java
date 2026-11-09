package com.avicola.monolith.alertas.controller;

import com.avicola.monolith.alertas.dto.AlertaResponseDTO;
import com.avicola.monolith.alertas.service.AlertEngineService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/alertas")
public class AlertaController {

    private final AlertEngineService alertEngineService;

    public AlertaController(AlertEngineService alertEngineService) {
        this.alertEngineService = alertEngineService;
    }

    @GetMapping("/activas")
    public ResponseEntity<List<AlertaResponseDTO>> listarAlertasActivas() {
        List<AlertaResponseDTO> dtos = alertEngineService.listarAlertasActivas()
                .stream()
                .map(AlertaResponseDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/conteo")
    public ResponseEntity<Map<String, Long>> contarAlertasActivas() {
        return ResponseEntity.ok(Map.of("pendientes", alertEngineService.contarAlertasActivas()));
    }

    @PatchMapping("/{alertaId}/revisar")
    public ResponseEntity<AlertaResponseDTO> marcarRevisada(@PathVariable UUID alertaId) {
        return ResponseEntity.ok(AlertaResponseDTO.fromEntity(alertEngineService.marcarComoRevisada(alertaId)));
    }
}
