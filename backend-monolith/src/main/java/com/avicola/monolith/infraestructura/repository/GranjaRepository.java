package com.avicola.monolith.infraestructura.repository;

import com.avicola.monolith.infraestructura.model.Granja;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface GranjaRepository extends JpaRepository<Granja, UUID> {
}
