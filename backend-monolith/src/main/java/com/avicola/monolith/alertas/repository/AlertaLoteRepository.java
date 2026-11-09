package com.avicola.monolith.alertas.repository;

import com.avicola.monolith.alertas.model.AlertaLote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AlertaLoteRepository extends JpaRepository<AlertaLote, UUID> {
    List<AlertaLote> findByRevisadaFalseOrderByFechaHoraAlertaDesc();
    List<AlertaLote> findByLoteIdOrderByFechaHoraAlertaDesc(UUID loteId);
    long countByRevisadaFalse();
}
