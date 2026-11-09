package com.avicola.monolith.alertas;

import com.avicola.monolith.alertas.model.AlertaLote;
import com.avicola.monolith.alertas.model.NivelSeveridad;
import com.avicola.monolith.alertas.model.TipoAlerta;
import com.avicola.monolith.alertas.repository.AlertaLoteRepository;
import com.avicola.monolith.alertas.service.AlertEngineService;
import com.avicola.monolith.infraestructura.model.Galpon;
import com.avicola.monolith.lotes.model.Lote;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class AlertEngineServiceTest {

    private AlertaLoteRepository alertaRepo;
    private AlertEngineService alertEngineService;
    private Lote lotePrueba;

    @BeforeEach
    void setUp() {
        alertaRepo = Mockito.mock(AlertaLoteRepository.class);
        alertEngineService = new AlertEngineService(alertaRepo);

        Galpon galpon = new Galpon();
        galpon.setNumeroGalpon(1);

        lotePrueba = new Lote();
        lotePrueba.setId(UUID.randomUUID());
        lotePrueba.setCodigoLote("LOTE-TEST-01");
        lotePrueba.setGalpon(galpon);
    }

    @Test
    @DisplayName("Debe generar alerta CRITICA cuando la tasa de mortalidad diaria supera el 0.50%")
    void testAlertaMortalidadCritica() {
        BigDecimal tasaMortalidad = new BigDecimal("0.7500"); // 0.75% > 0.50%
        BigDecimal cda = new BigDecimal("110.00");
        BigDecimal aguaHoy = new BigDecimal("300.00");
        BigDecimal alimentoHoy = new BigDecimal("150.00");
        BigDecimal aguaAyer = new BigDecimal("310.00");

        alertEngineService.evaluarAlertasDiarias(lotePrueba, tasaMortalidad, cda, aguaHoy, alimentoHoy, aguaAyer);

        ArgumentCaptor<AlertaLote> captor = ArgumentCaptor.forClass(AlertaLote.class);
        verify(alertaRepo, atLeastOnce()).save(captor.capture());

        AlertaLote alertaGuardada = captor.getValue();
        assertEquals(TipoAlerta.MORTALIDAD_ALTA, alertaGuardada.getTipoAlerta());
        assertEquals(NivelSeveridad.CRITICO, alertaGuardada.getNivelSeveridad());
    }

    @Test
    @DisplayName("No debe generar alerta de mortalidad si la tasa esta en rango normal (< 0.50%)")
    void testMortalidadNormalSinAlerta() {
        BigDecimal tasaMortalidad = new BigDecimal("0.1500"); // 0.15% < 0.50%
        BigDecimal cda = new BigDecimal("110.00");
        BigDecimal aguaHoy = new BigDecimal("300.00");
        BigDecimal alimentoHoy = new BigDecimal("150.00"); // Relacion 2.0 (normal)
        BigDecimal aguaAyer = new BigDecimal("305.00"); // Caida < 2% (normal)

        alertEngineService.evaluarAlertasDiarias(lotePrueba, tasaMortalidad, cda, aguaHoy, alimentoHoy, aguaAyer);

        verify(alertaRepo, never()).save(any(AlertaLote.class));
    }
}
