package com.avicola.monolith.lotes.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public class NuevoLoteDTO {

    @NotNull(message = "El galponId es obligatorio")
    private UUID galponId;

    @NotBlank(message = "El codigo del lote es obligatorio")
    private String codigoLote;

    @Min(value = 1, message = "La posicion del lote debe estar entre 1 y 4")
    @Max(value = 4, message = "La posicion del lote debe estar entre 1 y 4")
    private int posicionEnGalpon;

    private String lineaGenetica = "Cobb 500";

    @NotNull(message = "La fecha de encasetamiento es obligatoria")
    private LocalDate fechaEncasetamiento;

    @Min(value = 1, message = "La cantidad inicial de aves debe ser positiva")
    private int cantidadInicialAves;

    @DecimalMin(value = "1.0", message = "El peso inicial promedio debe ser positivo")
    private BigDecimal pesoInicialPromedioG;

    public NuevoLoteDTO() {}

    public UUID getGalponId() { return galponId; }
    public void setGalponId(UUID galponId) { this.galponId = galponId; }

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

    public BigDecimal getPesoInicialPromedioG() { return pesoInicialPromedioG; }
    public void setPesoInicialPromedioG(BigDecimal pesoInicialPromedioG) { this.pesoInicialPromedioG = pesoInicialPromedioG; }
}
