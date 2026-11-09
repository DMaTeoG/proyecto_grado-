package com.avicola.monolith.lotes.dto;

import com.avicola.monolith.lotes.model.Lote;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

public class LoteResponseDTO {
    private UUID id;
    private UUID galponId;
    private int numeroGalpon;
    private String codigoLote;
    private int posicionEnGalpon;
    private String lineaGenetica;
    private LocalDate fechaEncasetamiento;
    private long diasDeVida;
    private int cantidadInicialAves;
    private int cantidadActualVivas;
    private BigDecimal pesoInicialPromedioG;
    private String estado;

    public LoteResponseDTO() {}

    public static LoteResponseDTO fromEntity(Lote lote) {
        LoteResponseDTO dto = new LoteResponseDTO();
        dto.setId(lote.getId());
        dto.setGalponId(lote.getGalpon().getId());
        dto.setNumeroGalpon(lote.getGalpon().getNumeroGalpon());
        dto.setCodigoLote(lote.getCodigoLote());
        dto.setPosicionEnGalpon(lote.getPosicionEnGalpon());
        dto.setLineaGenetica(lote.getLineaGenetica());
        dto.setFechaEncasetamiento(lote.getFechaEncasetamiento());
        long dias = ChronoUnit.DAYS.between(lote.getFechaEncasetamiento(), LocalDate.now()) + 1;
        dto.setDiasDeVida(Math.max(1, dias));
        dto.setCantidadInicialAves(lote.getCantidadInicialAves());
        dto.setCantidadActualVivas(lote.getCantidadActualVivas());
        dto.setPesoInicialPromedioG(lote.getPesoInicialPromedioG());
        dto.setEstado(lote.getEstado());
        return dto;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getGalponId() { return galponId; }
    public void setGalponId(UUID galponId) { this.galponId = galponId; }

    public int getNumeroGalpon() { return numeroGalpon; }
    public void setNumeroGalpon(int numeroGalpon) { this.numeroGalpon = numeroGalpon; }

    public String getCodigoLote() { return codigoLote; }
    public void setCodigoLote(String codigoLote) { this.codigoLote = codigoLote; }

    public int getPosicionEnGalpon() { return posicionEnGalpon; }
    public void setPosicionEnGalpon(int posicionEnGalpon) { this.posicionEnGalpon = posicionEnGalpon; }

    public String getLineaGenetica() { return lineaGenetica; }
    public void setLineaGenetica(String lineaGenetica) { this.lineaGenetica = lineaGenetica; }

    public LocalDate getFechaEncasetamiento() { return fechaEncasetamiento; }
    public void setFechaEncasetamiento(LocalDate fechaEncasetamiento) { this.fechaEncasetamiento = fechaEncasetamiento; }

    public long getDiasDeVida() { return diasDeVida; }
    public void setDiasDeVida(long diasDeVida) { this.diasDeVida = diasDeVida; }

    public int getCantidadInicialAves() { return cantidadInicialAves; }
    public void setCantidadInicialAves(int cantidadInicialAves) { this.cantidadInicialAves = cantidadInicialAves; }

    public int getCantidadActualVivas() { return cantidadActualVivas; }
    public void setCantidadActualVivas(int cantidadActualVivas) { this.cantidadActualVivas = cantidadActualVivas; }

    public BigDecimal getPesoInicialPromedioG() { return pesoInicialPromedioG; }
    public void setPesoInicialPromedioG(BigDecimal pesoInicialPromedioG) { this.pesoInicialPromedioG = pesoInicialPromedioG; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}
