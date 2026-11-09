package com.avicola.monolith.lotes;

import com.avicola.monolith.infraestructura.model.Galpon;
import com.avicola.monolith.infraestructura.repository.GalponRepository;
import com.avicola.monolith.lotes.dto.NuevoLoteDTO;
import com.avicola.monolith.lotes.repository.LoteRepository;
import com.avicola.monolith.lotes.service.LoteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

public class LoteServiceTest {

    private LoteRepository loteRepo;
    private GalponRepository galponRepo;
    private LoteService loteService;
    private UUID galponId;
    private Galpon galpon;

    @BeforeEach
    void setUp() {
        loteRepo = Mockito.mock(LoteRepository.class);
        galponRepo = Mockito.mock(GalponRepository.class);
        loteService = new LoteService(loteRepo, galponRepo);

        galponId = UUID.randomUUID();
        galpon = new Galpon();
        galpon.setId(galponId);
        galpon.setNumeroGalpon(1);
        galpon.setCapacidadTotalAves(12000);

        when(galponRepo.findById(galponId)).thenReturn(Optional.of(galpon));
    }

    @Test
    @DisplayName("Debe lanzar excepción si se intenta crear un 5to lote activo en un galpón")
    void testRestriccionMaximoCuatroLotes() {
        // Simular que el galpón ya tiene 4 lotes activos
        when(loteRepo.countByGalponIdAndEstado(galponId, "ACTIVO")).thenReturn(4L);

        NuevoLoteDTO dto = new NuevoLoteDTO();
        dto.setGalponId(galponId);
        dto.setCodigoLote("LOTE-05-EXCEDIDO");
        dto.setPosicionEnGalpon(1);
        dto.setFechaEncasetamiento(LocalDate.now());
        dto.setCantidadInicialAves(2500);
        dto.setPesoInicialPromedioG(new BigDecimal("42.00"));

        assertThrows(IllegalStateException.class, () -> loteService.crearLote(dto));
    }
}
