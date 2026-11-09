package com.avicola.monolith.operacion.model;

import com.avicola.monolith.iam.model.Usuario;
import com.avicola.monolith.lotes.model.Lote;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Table(name = "biometrias_peso")
public class BiometriaPeso {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "lote_id", nullable = false)
    private Lote lote;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(name = "fecha_pesaje", nullable = false)
    private LocalDate fechaPesaje;

    @Column(name = "dia_vida", nullable = false)
    private int diaVida;

    @Column(name = "peso_promedio_g", nullable = false, precision = 7, scale = 2)
    private BigDecimal pesoPromedioG;

    @Column(name = "ganancia_diaria_peso_g", precision = 6, scale = 2)
    private BigDecimal gananciaDiariaPesoG;

    @Column(name = "tamano_muestra_aves", nullable = false)
    private int tamanoMuestraAves;

    @Column(name = "registrado_en", updatable = false)
    private ZonedDateTime registradoEn = ZonedDateTime.now();

    public BiometriaPeso() {}

    public BiometriaPeso(Lote lote, Usuario usuario, LocalDate fechaPesaje, int diaVida,
                         BigDecimal pesoPromedioG, BigDecimal gananciaDiariaPesoG, int tamanoMuestraAves) {
        this.lote = lote;
        this.usuario = usuario;
        this.fechaPesaje = fechaPesaje;
        this.diaVida = diaVida;
        this.pesoPromedioG = pesoPromedioG;
        this.gananciaDiariaPesoG = gananciaDiariaPesoG;
        this.tamanoMuestraAves = tamanoMuestraAves;
        this.registradoEn = ZonedDateTime.now();
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public Lote getLote() { return lote; }
    public void setLote(Lote lote) { this.lote = lote; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public LocalDate getFechaPesaje() { return fechaPesaje; }
    public void setFechaPesaje(LocalDate fechaPesaje) { this.fechaPesaje = fechaPesaje; }

    public int getDiaVida() { return diaVida; }
    public void setDiaVida(int diaVida) { this.diaVida = diaVida; }

    public BigDecimal getPesoPromedioG() { return pesoPromedioG; }
    public void setPesoPromedioG(BigDecimal pesoPromedioG) { this.pesoPromedioG = pesoPromedioG; }

    public BigDecimal getGananciaDiariaPesoG() { return gananciaDiariaPesoG; }
    public void setGananciaDiariaPesoG(BigDecimal gananciaDiariaPesoG) { this.gananciaDiariaPesoG = gananciaDiariaPesoG; }

    public int getTamanoMuestraAves() { return tamanoMuestraAves; }
    public void setTamanoMuestraAves(int tamanoMuestraAves) { this.tamanoMuestraAves = tamanoMuestraAves; }

    public ZonedDateTime getRegistradoEn() { return registradoEn; }
    public void setRegistradoEn(ZonedDateTime registradoEn) { this.registradoEn = registradoEn; }
}
