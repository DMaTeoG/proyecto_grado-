package com.avicola.monolith.alertas.service;

import com.avicola.monolith.alertas.model.AlertaLote;
import com.avicola.monolith.alertas.model.NivelSeveridad;
import com.avicola.monolith.alertas.model.TipoAlerta;
import com.avicola.monolith.alertas.repository.AlertaLoteRepository;
import com.avicola.monolith.lotes.model.Lote;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;

@Service
public class AlertEngineService {

    private final AlertaLoteRepository alertaRepository;

    public AlertEngineService(AlertaLoteRepository alertaRepository) {
        this.alertaRepository = alertaRepository;
    }

    @Transactional
    public void evaluarAlertasDiarias(Lote lote, BigDecimal tasaMortalidadDiaria, BigDecimal cda,
                                      BigDecimal aguaConsumidaL, BigDecimal alimentoSuministradoKg,
                                      BigDecimal aguaDiaAnteriorL) {
        // 1. Alerta por Mortalidad Diaria Crítica (>= 0.50%)
        BigDecimal umbralMortalidad = new BigDecimal("0.5000");
        if (tasaMortalidadDiaria.compareTo(umbralMortalidad) >= 0) {
            String mensaje = String.format("Pico critico de mortalidad en Lote %s (Galpon %d): %.2f%% de bajas registradas hoy.",
                    lote.getCodigoLote(), lote.getGalpon().getNumeroGalpon(), tasaMortalidadDiaria);
            AlertaLote alerta = new AlertaLote(lote, TipoAlerta.MORTALIDAD_ALTA, NivelSeveridad.CRITICO,
                    mensaje, tasaMortalidadDiaria, umbralMortalidad);
            alertaRepository.save(alerta);
        }

        // 2. Alerta por Caída Abrupta en Consumo de Agua (> 20% de caída)
        if (aguaDiaAnteriorL != null && aguaDiaAnteriorL.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal caidaAguaPct = aguaDiaAnteriorL.subtract(aguaConsumidaL)
                    .multiply(BigDecimal.valueOf(100))
                    .divide(aguaDiaAnteriorL, 2, RoundingMode.HALF_UP);

            if (caidaAguaPct.compareTo(new BigDecimal("20.00")) >= 0) {
                String mensaje = String.format("Caida critica del %.1f%% en el consumo de agua en Lote %s. Posible taponamiento de niples o estres.",
                        caidaAguaPct, lote.getCodigoLote());
                AlertaLote alerta = new AlertaLote(lote, TipoAlerta.CONSUMO_AGUA_CRITICO, NivelSeveridad.CRITICO,
                        mensaje, aguaConsumidaL, aguaDiaAnteriorL);
                alertaRepository.save(alerta);
            }
        }

        // 3. Alerta por Relación Hídrica Anómala (Agua L / Alimento kg)
        if (alimentoSuministradoKg != null && alimentoSuministradoKg.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal relacionHidrica = aguaConsumidaL.divide(alimentoSuministradoKg, 2, RoundingMode.HALF_UP);
            if (relacionHidrica.compareTo(new BigDecimal("2.60")) > 0 || relacionHidrica.compareTo(new BigDecimal("1.40")) < 0) {
                String mensaje = String.format("Relacion agua/alimento anomala (%.2f L/kg) en Lote %s. Rango normal esperado: 1.8 a 2.0 L/kg.",
                        relacionHidrica, lote.getCodigoLote());
                AlertaLote alerta = new AlertaLote(lote, TipoAlerta.CONSUMO_ALIMENTO_ANORMAL, NivelSeveridad.ADVERTENCIA,
                        mensaje, relacionHidrica, new BigDecimal("2.00"));
                alertaRepository.save(alerta);
            }
        }
    }

    @Transactional
    public void evaluarAlertaICA(Lote lote, BigDecimal icaCalculado, int semanaVida) {
        BigDecimal umbralMaxIca = new BigDecimal("1.80");
        if (semanaVida >= 5 && icaCalculado.compareTo(umbralMaxIca) > 0) {
            String mensaje = String.format("Indice de Conversion Alimenticia ineficiente (ICA: %.2f) en Lote %s en semana %d.",
                    icaCalculado, lote.getCodigoLote(), semanaVida);
            AlertaLote alerta = new AlertaLote(lote, TipoAlerta.ICA_DEFICIENTE, NivelSeveridad.CRITICO,
                    mensaje, icaCalculado, umbralMaxIca);
            alertaRepository.save(alerta);
        }
    }

    public List<AlertaLote> listarAlertasActivas() {
        return alertaRepository.findByRevisadaFalseOrderByFechaHoraAlertaDesc();
    }

    public long contarAlertasActivas() {
        return alertaRepository.countByRevisadaFalse();
    }

    @Transactional
    public AlertaLote marcarComoRevisada(UUID alertaId) {
        AlertaLote alerta = alertaRepository.findById(alertaId)
                .orElseThrow(() -> new IllegalArgumentException("Alerta no encontrada"));
        alerta.setRevisada(true);
        return alertaRepository.save(alerta);
    }
}
