package com.avicola.monolith.zootecnia.dto;

import java.math.BigDecimal;
import java.util.UUID;

public class ReporteZootecnicoDTO {
    private UUID loteId;
    private String codigoLote;
    private int galponNumero;
    private int posicionEnGalpon;
    private int avesIniciales;
    private int avesVivas;
    private int totalBajas;
    private BigDecimal tasaMortalidadAcumulada;
    private BigDecimal totalAlimentoKg;
    private BigDecimal totalAguaLitros;
    private BigDecimal relacionHidrica;
    private BigDecimal pesoPromedioActualG;
    private BigDecimal gananciaDiariaPesoG;
    private BigDecimal indiceConversionAlimenticia;

    public ReporteZootecnicoDTO() {}

    public UUID getLoteId() { return loteId; }
    public void setLoteId(UUID loteId) { this.loteId = loteId; }

    public String getCodigoLote() { return codigoLote; }
    public void setCodigoLote(String codigoLote) { this.codigoLote = codigoLote; }

    public int getGalponNumero() { return galponNumero; }
    public void setGalponNumero(int galponNumero) { this.galponNumero = galponNumero; }

    public int getPosicionEnGalpon() { return posicionEnGalpon; }
    public void setPosicionEnGalpon(int posicionEnGalpon) { this.posicionEnGalpon = posicionEnGalpon; }

    public int getAvesIniciales() { return avesIniciales; }
    public void setAvesIniciales(int avesIniciales) { this.avesIniciales = avesIniciales; }

    public int getAvesVivas() { return avesVivas; }
    public void setAvesVivas(int avesVivas) { this.avesVivas = avesVivas; }

    public int getTotalBajas() { return totalBajas; }
    public void setTotalBajas(int totalBajas) { this.totalBajas = totalBajas; }

    public BigDecimal getTasaMortalidadAcumulada() { return tasaMortalidadAcumulada; }
    public void setTasaMortalidadAcumulada(BigDecimal tasaMortalidadAcumulada) { this.tasaMortalidadAcumulada = tasaMortalidadAcumulada; }

    public BigDecimal getTotalAlimentoKg() { return totalAlimentoKg; }
    public void setTotalAlimentoKg(BigDecimal totalAlimentoKg) { this.totalAlimentoKg = totalAlimentoKg; }

    public BigDecimal getTotalAguaLitros() { return totalAguaLitros; }
    public void setTotalAguaLitros(BigDecimal totalAguaLitros) { this.totalAguaLitros = totalAguaLitros; }

    public BigDecimal getRelacionHidrica() { return relacionHidrica; }
    public void setRelacionHidrica(BigDecimal relacionHidrica) { this.relacionHidrica = relacionHidrica; }

    public BigDecimal getPesoPromedioActualG() { return pesoPromedioActualG; }
    public void setPesoPromedioActualG(BigDecimal pesoPromedioActualG) { this.pesoPromedioActualG = pesoPromedioActualG; }

    public BigDecimal getGananciaDiariaPesoG() { return gananciaDiariaPesoG; }
    public void setGananciaDiariaPesoG(BigDecimal gananciaDiariaPesoG) { this.gananciaDiariaPesoG = gananciaDiariaPesoG; }

    public BigDecimal getIndiceConversionAlimenticia() { return indiceConversionAlimenticia; }
    public void setIndiceConversionAlimenticia(BigDecimal indiceConversionAlimenticia) { this.indiceConversionAlimenticia = indiceConversionAlimenticia; }
}
