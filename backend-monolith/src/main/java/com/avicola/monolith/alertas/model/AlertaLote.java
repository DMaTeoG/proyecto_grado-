package com.avicola.monolith.alertas.model;

import com.avicola.monolith.lotes.model.Lote;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Table(name = "alertas_lote")
public class AlertaLote {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "lote_id", nullable = false)
    private Lote lote;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_alerta", nullable = false, length = 50)
    private TipoAlerta tipoAlerta;

    @Enumerated(EnumType.STRING)
    @Column(name = "nivel_severidad", nullable = false, length = 20)
    private NivelSeveridad nivelSeveridad;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String mensaje;

    @Column(name = "valor_registrado", nullable = false, precision = 10, scale = 2)
    private BigDecimal valorRegistrado;

    @Column(name = "valor_umbral_esperado", nullable = false, precision = 10, scale = 2)
    private BigDecimal valorUmbralEsperado;

    @Column(nullable = false)
    private boolean revisada = false;

    @Column(name = "fecha_hora_alerta", updatable = false)
    private ZonedDateTime fechaHoraAlerta = ZonedDateTime.now();

    public AlertaLote() {}

    public AlertaLote(Lote lote, TipoAlerta tipoAlerta, NivelSeveridad nivelSeveridad, String mensaje,
                      BigDecimal valorRegistrado, BigDecimal valorUmbralEsperado) {
        this.lote = lote;
        this.tipoAlerta = tipoAlerta;
        this.nivelSeveridad = nivelSeveridad;
        this.mensaje = mensaje;
        this.valorRegistrado = valorRegistrado;
        this.valorUmbralEsperado = valorUmbralEsperado;
        this.revisada = false;
        this.fechaHoraAlerta = ZonedDateTime.now();
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public Lote getLote() { return lote; }
    public void setLote(Lote lote) { this.lote = lote; }

    public TipoAlerta getTipoAlerta() { return tipoAlerta; }
    public void setTipoAlerta(TipoAlerta tipoAlerta) { this.tipoAlerta = tipoAlerta; }

    public NivelSeveridad getNivelSeveridad() { return nivelSeveridad; }
    public void setNivelSeveridad(NivelSeveridad nivelSeveridad) { this.nivelSeveridad = nivelSeveridad; }

    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }

    public BigDecimal getValorRegistrado() { return valorRegistrado; }
    public void setValorRegistrado(BigDecimal valorRegistrado) { this.valorRegistrado = valorRegistrado; }

    public BigDecimal getValorUmbralEsperado() { return valorUmbralEsperado; }
    public void setValorUmbralEsperado(BigDecimal valorUmbralEsperado) { this.valorUmbralEsperado = valorUmbralEsperado; }

    public boolean isRevisada() { return revisada; }
    public void setRevisada(boolean revisada) { this.revisada = revisada; }

    public ZonedDateTime getFechaHoraAlerta() { return fechaHoraAlerta; }
    public void setFechaHoraAlerta(ZonedDateTime fechaHoraAlerta) { this.fechaHoraAlerta = fechaHoraAlerta; }
}
