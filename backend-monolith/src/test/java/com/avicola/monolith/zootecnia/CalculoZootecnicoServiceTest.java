package com.avicola.monolith.zootecnia;

import com.avicola.monolith.lotes.model.Lote;
import com.avicola.monolith.operacion.model.RegistroDiario;
import com.avicola.monolith.operacion.repository.BiometriaPesoRepository;
import com.avicola.monolith.operacion.repository.RegistroDiarioRepository;
import com.avicola.monolith.lotes.repository.LoteRepository;
import com.avicola.monolith.zootecnia.service.CalculoZootecnicoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class CalculoZootecnicoServiceTest {

    private RegistroDiarioRepository registroRepo;
    private BiometriaPesoRepository biometriaRepo;
    private LoteRepository loteRepo;
    private CalculoZootecnicoService service;

    @BeforeEach
    void setUp() {
        registroRepo = Mockito.mock(RegistroDiarioRepository.class);
        biometriaRepo = Mockito.mock(BiometriaPesoRepository.class);
        loteRepo = Mockito.mock(LoteRepository.class);
        service = new CalculoZootecnicoService(registroRepo, biometriaRepo, loteRepo);
    }

    @Test
    @DisplayName("Debe calcular exactamente la tasa de mortalidad diaria porcentual")
    void testCalculoTasaMortalidad() {
        int avesMuertas = 15;
        int poblacionInicio = 3000;
        // (15 * 100) / 3000 = 0.5000%
        BigDecimal resultado = service.calcularTasaMortalidadDiaria(avesMuertas, poblacionInicio);
        assertEquals(new BigDecimal("0.5000"), resultado);
    }

    @Test
    @DisplayName("Debe calcular exactamente el Consumo Diario por Ave (CDA) en gramos")
    void testCalculoCDA() {
        BigDecimal alimentoKg = new BigDecimal("360.00");
        int avesVivas = 3000;
        // (360 * 1000) / 3000 = 120.00 g/ave/dia
        BigDecimal cda = service.calcularConsumoDiarioPorAve(alimentoKg, avesVivas);
        assertEquals(new BigDecimal("120.00"), cda);
    }

    @Test
    @DisplayName("Debe calcular exactamente la Ganancia Diaria de Peso (GDP)")
    void testCalculoGDP() {
        BigDecimal pesoActualG = new BigDecimal("950.00");
        BigDecimal pesoPrevioG = new BigDecimal("600.00");
        int dias = 7;
        // (950 - 600) / 7 = 350 / 7 = 50.00 g/dia
        BigDecimal gdp = service.calcularGananciaDiariaPeso(pesoActualG, pesoPrevioG, dias);
        assertEquals(new BigDecimal("50.00"), gdp);
    }

    @Test
    @DisplayName("Debe calcular correctamente el ICA con acumulado de alimento y biomasa viva")
    void testCalculoICA() {
        Lote lote = new Lote();
        lote.setId(UUID.randomUUID());
        lote.setCantidadActualVivas(2950);

        RegistroDiario r1 = new RegistroDiario();
        r1.setAlimentoSuministradoKg(new BigDecimal("250.00"));
        RegistroDiario r2 = new RegistroDiario();
        r2.setAlimentoSuministradoKg(new BigDecimal("290.00"));

        when(registroRepo.findByLoteIdOrderByFechaRegistroAsc(lote.getId()))
                .thenReturn(Arrays.asList(r1, r2));

        // Peso promedio = 1200 g (1.200 kg) -> Biomasa = 2950 * 1.200 = 3540 kg
        // Total alimento = 540 kg -> ICA = 540 / 3540 = 0.153
        BigDecimal pesoActualG = new BigDecimal("1200.00");
        BigDecimal ica = service.calcularIndiceConversionAlimenticia(lote, pesoActualG);

        assertEquals(new BigDecimal("0.153"), ica);
    }
}
