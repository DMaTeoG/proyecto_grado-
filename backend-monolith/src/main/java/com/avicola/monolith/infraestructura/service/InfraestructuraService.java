package com.avicola.monolith.infraestructura.service;

import com.avicola.monolith.infraestructura.dto.GalponDTO;
import com.avicola.monolith.infraestructura.dto.GranjaDTO;
import com.avicola.monolith.infraestructura.model.Galpon;
import com.avicola.monolith.infraestructura.model.Granja;
import com.avicola.monolith.infraestructura.repository.GalponRepository;
import com.avicola.monolith.infraestructura.repository.GranjaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class InfraestructuraService {

    private final GranjaRepository granjaRepository;
    private final GalponRepository galponRepository;

    public InfraestructuraService(GranjaRepository granjaRepository, GalponRepository galponRepository) {
        this.granjaRepository = granjaRepository;
        this.galponRepository = galponRepository;
    }

    public List<Granja> listarGranjas() {
        return granjaRepository.findAll();
    }

    public Granja obtenerGranja(UUID id) {
        return granjaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Granja no encontrada"));
    }

    @Transactional
    public Granja crearGranja(GranjaDTO dto) {
        Granja granja = new Granja(dto.getNombre(), dto.getUbicacion());
        return granjaRepository.save(granja);
    }

    public List<Galpon> listarGalponesPorGranja(UUID granjaId) {
        return galponRepository.findByGranjaIdOrderByNumeroGalponAsc(granjaId);
    }

    public Galpon obtenerGalpon(UUID id) {
        return galponRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Galpon no encontrado"));
    }

    @Transactional
    public Galpon crearGalpon(GalponDTO dto) {
        Granja granja = obtenerGranja(dto.getGranjaId());

        if (galponRepository.findByGranjaIdAndNumeroGalpon(dto.getGranjaId(), dto.getNumeroGalpon()).isPresent()) {
            throw new IllegalArgumentException("El galpon numero " + dto.getNumeroGalpon() + " ya existe en esta granja");
        }

        Galpon galpon = new Galpon(granja, dto.getNumeroGalpon(), dto.getCapacidadTotalAves());
        return galponRepository.save(galpon);
    }
}
