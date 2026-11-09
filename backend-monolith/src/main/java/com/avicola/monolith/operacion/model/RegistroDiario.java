package com.avicola.monolith.operacion.model;

import com.avicola.monolith.iam.model.Usuario;
import com.avicola.monolith.lotes.model.Lote;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Table(name = "registros_diarios", uniqueConstraints = {
    @UniqueConstraint(name = "uk_lote_fecha_registro", columnNames = {"lote_id", "fecha_registro"})
})
public class RegistroDiario {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "lote_id", nullable = false)
    private Lote lote;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(name = "fecha_registro", nullable = false)
    private LocalDate fechaRegistro;

    @Column(name = "dia_vida", nullable = false)
    private int diaVida;

    @Column(name = "aves_muertas", nullable = false)
    private int avesMuertas = 0;

    @Column(name = "alimento_suministrado_kg", nullable = false, precision = 8, scale = 2)
    private BigDecimal alimentoSuministradoKg = BigDecimal.ZERO;

    @Column(name = "agua_consumida_litros", nullable = false, precision = 8, scale = 2)
    private BigDecimal aguaConsumidaLitros = BigDecimal.ZERO;

    @Column(columnDefinition = "TEXT")
    private String observaciones;

    @Column(name = "registrado_en", updatable = false)
    private ZonedDateTime registradoEn = ZonedDateTime.now();

    public RegistroDiario() {}

    public RegistroDiario(Lote lote, Usuario usuario, LocalDate fechaRegistro, int diaVida,
                          int avesMuertas, BigDecimal alimentoSuministradoKg,
                          BigDecimal aguaConsumidaLitros, String observaciones) {
        this.lote = lote;
        this.usuario = usuario;
        this.fechaRegistro = fechaRegistro;
        this.diaVida = diaVida;
        this.avesMuertas = avesMuertas;
        this.alimentoSuministradoKg = alimentoSuministradoKg;
        this.aguaConsumidaLitros = aguaConsumidaLitros;
        this.observaciones = observaciones;
        this.registradoEn = ZonedDateTime.now();
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public Lote getLote() { return lote; }
    public void setLote(Lote lote) { this.lote = lote; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public LocalDate getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(LocalDate fechaRegistro) { this.fechaRegistro = fechaRegistro; }

    public int getDiaVida() { return diaVida; }
    public void setDiaVida(int diaVida) { this.diaVida = diaVida; }

    public int getAvesMuertas() { return avesMuertas; }
    public void setAvesMuertas(int avesMuertas) { this.avesMuertas = avesMuertas; }

    public BigDecimal getAlimentoSuministradoKg() { return alimentoSuministradoKg; }
    public void setAlimentoSuministradoKg(BigDecimal alimentoSuministradoKg) { this.alimentoSuministradoKg = alimentoSuministradoKg; }

    public BigDecimal getAguaConsumidaLitros() { return aguaConsumidaLitros; }
    public void setAguaConsumidaLitros(BigDecimal aguaConsumidaLitros) { this.aguaConsumidaLitros = aguaConsumidaLitros; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    public ZonedDateTime getRegistradoEn() { return registradoEn; }
    public void setRegistradoEn(ZonedDateTime registradoEn) { this.registradoEn = registradoEn; }
}
