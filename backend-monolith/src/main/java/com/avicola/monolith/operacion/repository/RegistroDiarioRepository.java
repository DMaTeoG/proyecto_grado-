package com.avicola.monolith.operacion.repository;

import com.avicola.monolith.operacion.model.RegistroDiario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RegistroDiarioRepository extends JpaRepository<RegistroDiario, UUID> {
    List<RegistroDiario> findByLoteIdOrderByFechaRegistroAsc(UUID loteId);
    Optional<RegistroDiario> findByLoteIdAndFechaRegistro(UUID loteId, LocalDate fechaRegistro);
    Optional<RegistroDiario> findTopByLoteIdOrderByFechaRegistroDesc(UUID loteId);
}
