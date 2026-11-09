package com.avicola.monolith.operacion.repository;

import com.avicola.monolith.operacion.model.BiometriaPeso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BiometriaPesoRepository extends JpaRepository<BiometriaPeso, UUID> {
    List<BiometriaPeso> findByLoteIdOrderByFechaPesajeAsc(UUID loteId);
    Optional<BiometriaPeso> findTopByLoteIdOrderByFechaPesajeDesc(UUID loteId);
}
