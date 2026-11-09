package com.avicola.monolith.operacion.service;

import com.avicola.monolith.alertas.service.AlertEngineService;
import com.avicola.monolith.iam.model.Usuario;
import com.avicola.monolith.iam.repository.UsuarioRepository;
import com.avicola.monolith.lotes.model.Lote;
import com.avicola.monolith.lotes.repository.LoteRepository;
import com.avicola.monolith.operacion.dto.NuevaBiometriaDTO;
import com.avicola.monolith.operacion.dto.NuevoRegistroDiarioDTO;
import com.avicola.monolith.operacion.model.BiometriaPeso;
import com.avicola.monolith.operacion.model.RegistroDiario;
import com.avicola.monolith.operacion.repository.BiometriaPesoRepository;
import com.avicola.monolith.operacion.repository.RegistroDiarioRepository;
import com.avicola.monolith.zootecnia.service.CalculoZootecnicoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class RegistroOperativoService {

    private final RegistroDiarioRepository registroRepo;
    private final BiometriaPesoRepository biometriaRepo;
    private final LoteRepository loteRepo;
    private final UsuarioRepository usuarioRepo;
    private final CalculoZootecnicoService zootecniaService;
    private final AlertEngineService alertService;

    public RegistroOperativoService(RegistroDiarioRepository registroRepo,
                                    BiometriaPesoRepository biometriaRepo,
                                    LoteRepository loteRepo,
                                    UsuarioRepository usuarioRepo,
                                    CalculoZootecnicoService zootecniaService,
                                    AlertEngineService alertService) {
        this.registroRepo = registroRepo;
        this.biometriaRepo = biometriaRepo;
        this.loteRepo = loteRepo;
        this.usuarioRepo = usuarioRepo;
        this.zootecniaService = zootecniaService;
        this.alertService = alertService;
    }

    @Transactional
    public RegistroDiario crearRegistroDiario(NuevoRegistroDiarioDTO dto, String userEmail) {
        Lote lote = loteRepo.findById(dto.getLoteId())
                .orElseThrow(() -> new IllegalArgumentException("Lote no encontrado"));

        if (!"ACTIVO".equalsIgnoreCase(lote.getEstado())) {
            throw new IllegalStateException("No se pueden ingresar registros en un lote CERRADO");
        }

        if (registroRepo.findByLoteIdAndFechaRegistro(dto.getLoteId(), dto.getFechaRegistro()).isPresent()) {
            throw new IllegalArgumentException("Ya existe un registro para este lote en la fecha " + dto.getFechaRegistro());
        }

        Usuario usuario = usuarioRepo.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no autenticado"));

        int poblacionAntes = lote.getCantidadActualVivas();
        if (dto.getAvesMuertas() > poblacionAntes) {
            throw new IllegalArgumentException("La cantidad de bajas (" + dto.getAvesMuertas() + ") supera las aves vivas (" + poblacionAntes + ")");
        }

        // 1. Descuento atómico de población viva
        lote.setCantidadActualVivas(poblacionAntes - dto.getAvesMuertas());
        loteRepo.save(lote);

        // 2. Persistencia del registro diario
        RegistroDiario registro = new RegistroDiario(
                lote,
                usuario,
                dto.getFechaRegistro(),
                dto.getDiaVida(),
                dto.getAvesMuertas(),
                dto.getAlimentoSuministradoKg(),
                dto.getAguaConsumidaLitros(),
                dto.getObservaciones()
        );
        RegistroDiario guardado = registroRepo.save(registro);

        // 3. Obtener consumo de agua previo para detectar caídas
        Optional<RegistroDiario> registroPrevio = registroRepo.findTopByLoteIdOrderByFechaRegistroDesc(lote.getId());
        BigDecimal aguaPreviaL = registroPrevio.map(RegistroDiario::getAguaConsumidaLitros).orElse(null);

        // 4. Ejecución del motor de inferencia de alertas
        BigDecimal tasaMortalidad = zootecniaService.calcularTasaMortalidadDiaria(dto.getAvesMuertas(), poblacionAntes);
        BigDecimal cda = zootecniaService.calcularConsumoDiarioPorAve(dto.getAlimentoSuministradoKg(), poblacionAntes);

        alertService.evaluarAlertasDiarias(lote, tasaMortalidad, cda, dto.getAguaConsumidaLitros(),
                dto.getAlimentoSuministradoKg(), aguaPreviaL);

        return guardado;
    }

    @Transactional
    public BiometriaPeso registrarBiometria(NuevaBiometriaDTO dto, String userEmail) {
        Lote lote = loteRepo.findById(dto.getLoteId())
                .orElseThrow(() -> new IllegalArgumentException("Lote no encontrado"));

        Usuario usuario = usuarioRepo.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no autenticado"));

        Optional<BiometriaPeso> pesajePrevio = biometriaRepo.findTopByLoteIdOrderByFechaPesajeDesc(lote.getId());

        BigDecimal pesoAnteriorG = pesajePrevio.map(BiometriaPeso::getPesoPromedioG)
                .orElse(lote.getPesoInicialPromedioG());

        int diasTranscurridos = pesajePrevio.map(p -> (int) ChronoUnit.DAYS.between(p.getFechaPesaje(), dto.getFechaPesaje()))
                .orElse(dto.getDiaVida());

        BigDecimal gdp = zootecniaService.calcularGananciaDiariaPeso(dto.getPesoPromedioG(), pesoAnteriorG, diasTranscurridos);

        BiometriaPeso biometria = new BiometriaPeso(
                lote,
                usuario,
                dto.getFechaPesaje(),
                dto.getDiaVida(),
                dto.getPesoPromedioG(),
                gdp,
                dto.getTamanoMuestraAves()
        );

        BiometriaPeso guardada = biometriaRepo.save(biometria);

        // Evaluar alerta de ICA semanal
        BigDecimal ica = zootecniaService.calcularIndiceConversionAlimenticia(lote, dto.getPesoPromedioG());
        int semanaVida = (dto.getDiaVida() / 7) + 1;
        alertService.evaluarAlertaICA(lote, ica, semanaVida);

        return guardada;
    }

    public List<RegistroDiario> listarRegistrosPorLote(UUID loteId) {
        return registroRepo.findByLoteIdOrderByFechaRegistroAsc(loteId);
    }

    public List<BiometriaPeso> listarBiometriasPorLote(UUID loteId) {
        return biometriaRepo.findByLoteIdOrderByFechaPesajeAsc(loteId);
    }
}
