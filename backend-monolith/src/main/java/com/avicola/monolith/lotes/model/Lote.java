package com.avicola.monolith.lotes.model;

import com.avicola.monolith.infraestructura.model.Galpon;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Table(name = "lotes", uniqueConstraints = {
    @UniqueConstraint(name = "uk_lote_codigo", columnNames = {"codigo_lote"}),
    @UniqueConstraint(name = "uk_galpon_posicion_activo", columnNames = {"galpon_id", "posicion_en_galpon", "estado"})
})
public class Lote {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "galpon_id", nullable = false)
    private Galpon galpon;

    @Column(name = "codigo_lote", nullable = false, length = 50)
    private String codigoLote;

    @Column(name = "posicion_en_galpon", nullable = false)
    private int posicionEnGalpon; // 1, 2, 3 o 4

    @Column(name = "linea_genetica", nullable = false, length = 50)
    private String lineaGenetica = "Cobb 500";

    @Column(name = "fecha_encasetamiento", nullable = false)
    private LocalDate fechaEncasetamiento;

    @Column(name = "cantidad_inicial_aves", nullable = false)
    private int cantidadInicialAves;

    @Column(name = "cantidad_actual_vivas", nullable = false)
    private int cantidadActualVivas;

    @Column(name = "peso_inicial_promedio_g", nullable = false, precision = 6, scale = 2)
    private BigDecimal pesoInicialPromedioG;

    @Column(nullable = false, length = 20)
    private String estado = "ACTIVO"; // ACTIVO o CERRADO

    @Column(name = "creado_en", updatable = false)
    private ZonedDateTime creadoEn = ZonedDateTime.now();

    public Lote() {}

    public Lote(Galpon galpon, String codigoLote, int posicionEnGalpon, String lineaGenetica,
                LocalDate fechaEncasetamiento, int cantidadInicialAves, BigDecimal pesoInicialPromedioG) {
        this.galpon = galpon;
        this.codigoLote = codigoLote;
        this.posicionEnGalpon = posicionEnGalpon;
        this.lineaGenetica = lineaGenetica;
        this.fechaEncasetamiento = fechaEncasetamiento;
        this.cantidadInicialAves = cantidadInicialAves;
        this.cantidadActualVivas = cantidadInicialAves;
        this.pesoInicialPromedioG = pesoInicialPromedioG;
        this.estado = "ACTIVO";
        this.creadoEn = ZonedDateTime.now();
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public Galpon getGalpon() { return galpon; }
    public void setGalpon(Galpon galpon) { this.galpon = galpon; }

    public String getCodigoLote() { return codigoLote; }
    public void setCodigoLote(String codigoLote) { this.codigoLote = codigoLote; }

    public int getPosicionEnGalpon() { return posicionEnGalpon; }
    public void setPosicionEnGalpon(int posicionEnGalpon) { this.posicionEnGalpon = posicionEnGalpon; }

    public String getLineaGenetica() { return lineaGenetica; }
    public void setLineaGenetica(String lineaGenetica) { this.lineaGenetica = lineaGenetica; }

    public LocalDate getFechaEncasetamiento() { return fechaEncasetamiento; }
    public void setFechaEncasetamiento(LocalDate fechaEncasetamiento) { this.fechaEncasetamiento = fechaEncasetamiento; }

    public int getCantidadInicialAves() { return cantidadInicialAves; }
    public void setCantidadInicialAves(int cantidadInicialAves) { this.cantidadInicialAves = cantidadInicialAves; }

    public int getCantidadActualVivas() { return cantidadActualVivas; }
    public void setCantidadActualVivas(int cantidadActualVivas) { this.cantidadActualVivas = cantidadActualVivas; }

    public BigDecimal getPesoInicialPromedioG() { return pesoInicialPromedioG; }
    public void setPesoInicialPromedioG(BigDecimal pesoInicialPromedioG) { this.pesoInicialPromedioG = pesoInicialPromedioG; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public ZonedDateTime getCreadoEn() { return creadoEn; }
    public void setCreadoEn(ZonedDateTime creadoEn) { this.creadoEn = creadoEn; }
}
