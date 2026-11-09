package com.avicola.monolith.operacion.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public class NuevaBiometriaDTO {

    @NotNull(message = "El loteId es obligatorio")
    private UUID loteId;

    @NotNull(message = "La fecha de pesaje es obligatoria")
    private LocalDate fechaPesaje;

    @Min(value = 1, message = "El dia de vida debe ser >= 1")
    private int diaVida;

    @NotNull(message = "El peso promedio es obligatorio")
    @DecimalMin(value = "1.0", message = "El peso promedio debe ser positivo")
    private BigDecimal pesoPromedioG;

    @Min(value = 5, message = "El tamano de muestra debe ser de al menos 5 aves")
    private int tamanoMuestraAves;

    public NuevaBiometriaDTO() {}

    public UUID getLoteId() { return loteId; }
    public void setLoteId(UUID loteId) { this.loteId = loteId; }

    public LocalDate getFechaPesaje() { return fechaPesaje; }
    public void setFechaPesaje(LocalDate fechaPesaje) { this.fechaPesaje = fechaPesaje; }

    public int getDiaVida() { return diaVida; }
    public void setDiaVida(int diaVida) { this.diaVida = diaVida; }

    public BigDecimal getPesoPromedioG() { return pesoPromedioG; }
    public void setPesoPromedioG(BigDecimal pesoPromedioG) { this.pesoPromedioG = pesoPromedioG; }

    public int getTamanoMuestraAves() { return tamanoMuestraAves; }
    public void setTamanoMuestraAves(int tamanoMuestraAves) { this.tamanoMuestraAves = tamanoMuestraAves; }
}
