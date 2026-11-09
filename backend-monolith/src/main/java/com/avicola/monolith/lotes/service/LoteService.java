package com.avicola.monolith.lotes.service;

import com.avicola.monolith.infraestructura.model.Galpon;
import com.avicola.monolith.infraestructura.repository.GalponRepository;
import com.avicola.monolith.lotes.dto.LoteResponseDTO;
import com.avicola.monolith.lotes.dto.NuevoLoteDTO;
import com.avicola.monolith.lotes.model.Lote;
import com.avicola.monolith.lotes.repository.LoteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class LoteService {

    private final LoteRepository loteRepository;
    private final GalponRepository galponRepository;

    public LoteService(LoteRepository loteRepository, GalponRepository galponRepository) {
        this.loteRepository = loteRepository;
        this.galponRepository = galponRepository;
    }

    public List<LoteResponseDTO> listarLotesPorGalpon(UUID galponId) {
        return loteRepository.findByGalponIdOrderByPosicionEnGalponAsc(galponId)
                .stream()
                .map(LoteResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public List<LoteResponseDTO> listarLotesActivosPorGalpon(UUID galponId) {
        return loteRepository.findByGalponIdAndEstadoOrderByPosicionEnGalponAsc(galponId, "ACTIVO")
                .stream()
                .map(LoteResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public Lote obtenerLotePorId(UUID id) {
        return loteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Lote no encontrado"));
    }

    @Transactional
    public LoteResponseDTO crearLote(NuevoLoteDTO dto) {
        Galpon galpon = galponRepository.findById(dto.getGalponId())
                .orElseThrow(() -> new IllegalArgumentException("Galpon no encontrado"));

        // REGLA DE NEGOCIO: Maximo 4 lotes activos por galpon
        long lotesActivos = loteRepository.countByGalponIdAndEstado(dto.getGalponId(), "ACTIVO");
        if (lotesActivos >= 4) {
            throw new IllegalStateException("Capacidad maxima de 4 lotes alcanzada para este galpon");
        }

        // Validar que la posicion (1, 2, 3 o 4) no este ocupada por otro lote activo
        if (loteRepository.findByGalponIdAndPosicionEnGalponAndEstado(dto.getGalponId(), dto.getPosicionEnGalpon(), "ACTIVO").isPresent()) {
            throw new IllegalArgumentException("La posicion " + dto.getPosicionEnGalpon() + " ya se encuentra ocupada por un lote activo en este galpon");
        }

        if (loteRepository.findByCodigoLote(dto.getCodigoLote()).isPresent()) {
            throw new IllegalArgumentException("El codigo de lote " + dto.getCodigoLote() + " ya existe");
        }

        Lote lote = new Lote(
                galpon,
                dto.getCodigoLote(),
                dto.getPosicionEnGalpon(),
                dto.getLineaGenetica(),
                dto.getFechaEncasetamiento(),
                dto.getCantidadInicialAves(),
                dto.getPesoInicialPromedioG()
        );

        Lote guardado = loteRepository.save(lote);
        return LoteResponseDTO.fromEntity(guardado);
    }

    @Transactional
    public LoteResponseDTO cerrarLote(UUID loteId) {
        Lote lote = obtenerLotePorId(loteId);
        lote.setEstado("CERRADO");
        Lote actualizado = loteRepository.save(lote);
        return LoteResponseDTO.fromEntity(actualizado);
    }
}
