package com.avicola.monolith.zootecnia.service;

import com.avicola.monolith.lotes.model.Lote;
import com.avicola.monolith.lotes.repository.LoteRepository;
import com.avicola.monolith.operacion.model.BiometriaPeso;
import com.avicola.monolith.operacion.model.RegistroDiario;
import com.avicola.monolith.operacion.repository.BiometriaPesoRepository;
import com.avicola.monolith.operacion.repository.RegistroDiarioRepository;
import com.avicola.monolith.zootecnia.dto.ReporteZootecnicoDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;

@Service
public class CalculoZootecnicoService {

    private final RegistroDiarioRepository registroRepo;
    private final BiometriaPesoRepository biometriaRepo;
    private final LoteRepository loteRepo;

    public CalculoZootecnicoService(RegistroDiarioRepository registroRepo,
                                    BiometriaPesoRepository biometriaRepo,
                                    LoteRepository loteRepo) {
        this.registroRepo = registroRepo;
        this.biometriaRepo = biometriaRepo;
        this.loteRepo = loteRepo;
    }

    /**
     * Calcula el Consumo Diario por Ave (CDA) en g/ave/dia
     */
    public BigDecimal calcularConsumoDiarioPorAve(BigDecimal alimentoKg, int avesVivas) {
        if (avesVivas <= 0 || alimentoKg == null || alimentoKg.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }
        return alimentoKg.multiply(BigDecimal.valueOf(1000))
                .divide(BigDecimal.valueOf(avesVivas), 2, RoundingMode.HALF_UP);
    }

    /**
     * Calcula la Tasa de Mortalidad Diaria (%)
     */
    public BigDecimal calcularTasaMortalidadDiaria(int muertas, int poblacionInicio) {
        if (poblacionInicio <= 0 || muertas <= 0) {
            return BigDecimal.ZERO;
        }
        return BigDecimal.valueOf(muertas)
                .multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(poblacionInicio), 4, RoundingMode.HALF_UP);
    }

    /**
     * Calcula la Tasa de Mortalidad Acumulada (%)
     */
    public BigDecimal calcularTasaMortalidadAcumulada(int totalBajas, int cantidadInicial) {
        if (cantidadInicial <= 0 || totalBajas <= 0) {
            return BigDecimal.ZERO;
        }
        return BigDecimal.valueOf(totalBajas)
                .multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(cantidadInicial), 2, RoundingMode.HALF_UP);
    }

    /**
     * Calcula la Ganancia Diaria de Peso (GDP) en g/dia
     */
    public BigDecimal calcularGananciaDiariaPeso(BigDecimal pesoActualG, BigDecimal pesoAnteriorG, int diasTranscurridos) {
        if (diasTranscurridos <= 0 || pesoActualG == null || pesoAnteriorG == null) {
            return BigDecimal.ZERO;
        }
        return pesoActualG.subtract(pesoAnteriorG)
                .divide(BigDecimal.valueOf(diasTranscurridos), 2, RoundingMode.HALF_UP);
    }

    /**
     * Calcula el Índice de Conversión Alimenticia (ICA)
     */
    public BigDecimal calcularIndiceConversionAlimenticia(Lote lote, BigDecimal pesoPromedioActualG) {
        if (pesoPromedioActualG == null || pesoPromedioActualG.compareTo(BigDecimal.ZERO) <= 0 || lote.getCantidadActualVivas() <= 0) {
            return BigDecimal.ZERO;
        }

        List<RegistroDiario> registros = registroRepo.findByLoteIdOrderByFechaRegistroAsc(lote.getId());
        BigDecimal totalAlimentoKg = registros.stream()
                .map(RegistroDiario::getAlimentoSuministradoKg)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal pesoPromedioKg = pesoPromedioActualG.divide(BigDecimal.valueOf(1000), 4, RoundingMode.HALF_UP);
        BigDecimal biomasaTotalKg = pesoPromedioKg.multiply(BigDecimal.valueOf(lote.getCantidadActualVivas()));

        if (biomasaTotalKg.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }

        return totalAlimentoKg.divide(biomasaTotalKg, 3, RoundingMode.HALF_UP);
    }

    @Transactional(readOnly = true)
    public ReporteZootecnicoDTO generarReporteZootecnico(UUID loteId) {
        Lote lote = loteRepo.findById(loteId)
                .orElseThrow(() -> new IllegalArgumentException("Lote no encontrado"));

        List<RegistroDiario> registros = registroRepo.findByLoteIdOrderByFechaRegistroAsc(loteId);
        List<BiometriaPeso> biometrias = biometriaRepo.findByLoteIdOrderByFechaPesajeAsc(loteId);

        BigDecimal totalAlimentoKg = registros.stream()
                .map(RegistroDiario::getAlimentoSuministradoKg)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalAguaL = registros.stream()
                .map(RegistroDiario::getAguaConsumidaLitros)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        int totalMuertas = registros.stream()
                .mapToInt(RegistroDiario::getAvesMuertas)
                .sum();

        BigDecimal pesoActualG = biometrias.isEmpty()
                ? lote.getPesoInicialPromedioG()
                : biometrias.get(biometrias.size() - 1).getPesoPromedioG();

        BigDecimal gdpActual = biometrias.isEmpty()
                ? BigDecimal.ZERO
                : biometrias.get(biometrias.size() - 1).getGananciaDiariaPesoG();

        BigDecimal ica = calcularIndiceConversionAlimenticia(lote, pesoActualG);
        BigDecimal mortalidadAcumulada = calcularTasaMortalidadAcumulada(totalMuertas, lote.getCantidadInicialAves());

        BigDecimal relacionHidrica = totalAlimentoKg.compareTo(BigDecimal.ZERO) > 0
                ? totalAguaL.divide(totalAlimentoKg, 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        ReporteZootecnicoDTO dto = new ReporteZootecnicoDTO();
        dto.setLoteId(lote.getId());
        dto.setCodigoLote(lote.getCodigoLote());
        dto.setGalponNumero(lote.getGalpon().getNumeroGalpon());
        dto.setPosicionEnGalpon(lote.getPosicionEnGalpon());
        dto.setAvesIniciales(lote.getCantidadInicialAves());
        dto.setAvesVivas(lote.getCantidadActualVivas());
        dto.setTotalBajas(totalMuertas);
        dto.setTasaMortalidadAcumulada(mortalidadAcumulada);
        dto.setTotalAlimentoKg(totalAlimentoKg);
        dto.setTotalAguaLitros(totalAguaL);
        dto.setRelacionHidrica(relacionHidrica);
        dto.setPesoPromedioActualG(pesoActualG);
        dto.setGananciaDiariaPesoG(gdpActual != null ? gdpActual : BigDecimal.ZERO);
        dto.setIndiceConversionAlimenticia(ica);

        return dto;
    }
}
