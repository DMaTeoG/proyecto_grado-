package com.avicola.monolith.lotes.repository;

import com.avicola.monolith.lotes.model.Lote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface LoteRepository extends JpaRepository<Lote, UUID> {
    List<Lote> findByGalponIdOrderByPosicionEnGalponAsc(UUID galponId);
    List<Lote> findByGalponIdAndEstadoOrderByPosicionEnGalponAsc(UUID galponId, String estado);
    long countByGalponIdAndEstado(UUID galponId, String estado);
    Optional<Lote> findByCodigoLote(String codigoLote);
    Optional<Lote> findByGalponIdAndPosicionEnGalponAndEstado(UUID galponId, int posicion, String estado);
}
