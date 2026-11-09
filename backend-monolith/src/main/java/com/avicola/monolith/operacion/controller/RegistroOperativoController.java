package com.avicola.monolith.operacion.controller;

import com.avicola.monolith.operacion.dto.NuevaBiometriaDTO;
import com.avicola.monolith.operacion.dto.NuevoRegistroDiarioDTO;
import com.avicola.monolith.operacion.model.BiometriaPeso;
import com.avicola.monolith.operacion.model.RegistroDiario;
import com.avicola.monolith.operacion.service.RegistroOperativoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/operacion")
public class RegistroOperativoController {

    private final RegistroOperativoService operativoService;

    public RegistroOperativoController(RegistroOperativoService operativoService) {
        this.operativoService = operativoService;
    }

    @PostMapping("/diario")
    public ResponseEntity<RegistroDiario> registrarDiario(@Valid @RequestBody NuevoRegistroDiarioDTO dto,
                                                          Authentication authentication) {
        String email = authentication != null ? authentication.getName() : "admin@avicola.com";
        return ResponseEntity.status(HttpStatus.CREATED).body(operativoService.crearRegistroDiario(dto, email));
    }

    @PostMapping("/biometria")
    public ResponseEntity<BiometriaPeso> registrarBiometria(@Valid @RequestBody NuevaBiometriaDTO dto,
                                                            Authentication authentication) {
        String email = authentication != null ? authentication.getName() : "admin@avicola.com";
        return ResponseEntity.status(HttpStatus.CREATED).body(operativoService.registrarBiometria(dto, email));
    }

    @GetMapping("/lote/{loteId}/registros")
    public ResponseEntity<List<RegistroDiario>> listarRegistrosPorLote(@PathVariable UUID loteId) {
        return ResponseEntity.ok(operativoService.listarRegistrosPorLote(loteId));
    }

    @GetMapping("/lote/{loteId}/biometrias")
    public ResponseEntity<List<BiometriaPeso>> listarBiometriasPorLote(@PathVariable UUID loteId) {
        return ResponseEntity.ok(operativoService.listarBiometriasPorLote(loteId));
    }
}
