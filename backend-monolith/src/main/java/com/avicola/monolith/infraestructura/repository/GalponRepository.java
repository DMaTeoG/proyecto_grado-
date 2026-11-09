package com.avicola.monolith.infraestructura.repository;

import com.avicola.monolith.infraestructura.model.Galpon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface GalponRepository extends JpaRepository<Galpon, UUID> {
    List<Galpon> findByGranjaIdOrderByNumeroGalponAsc(UUID granjaId);
    Optional<Galpon> findByGranjaIdAndNumeroGalpon(UUID granjaId, int numeroGalpon);
}
