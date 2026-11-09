package com.avicola.monolith.operacion.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public class NuevoRegistroDiarioDTO {

    @NotNull(message = "El loteId es obligatorio")
    private UUID loteId;

    @NotNull(message = "La fecha de registro es obligatoria")
    private LocalDate fechaRegistro;

    @Min(value = 1, message = "El dia de vida debe ser >= 1")
    private int diaVida;

    @Min(value = 0, message = "Las aves muertas no pueden ser negativas")
    private int avesMuertas;

    @NotNull(message = "El alimento suministrado es obligatorio")
    @DecimalMin(value = "0.0", message = "El alimento no puede ser negativo")
    private BigDecimal alimentoSuministradoKg;

    @NotNull(message = "El agua consumida es obligatoria")
    @DecimalMin(value = "0.0", message = "El agua no puede ser negativa")
    private BigDecimal aguaConsumidaLitros;

    private String observaciones;

    public NuevoRegistroDiarioDTO() {}

    public UUID getLoteId() { return loteId; }
    public void setLoteId(UUID loteId) { this.loteId = loteId; }

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
}
