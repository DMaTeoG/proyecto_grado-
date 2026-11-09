package com.avicola.monolith.infraestructura.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public class GalponDTO {
    private UUID id;

    @NotNull(message = "El id de la granja es obligatorio")
    private UUID granjaId;

    @Min(value = 1, message = "El numero de galpon debe ser mayor a 0")
    private int numeroGalpon;

    @Min(value = 100, message = "La capacidad debe ser de al menos 100 aves")
    private int capacidadTotalAves;

    public GalponDTO() {}

    public GalponDTO(UUID id, UUID granjaId, int numeroGalpon, int capacidadTotalAves) {
        this.id = id;
        this.granjaId = granjaId;
        this.numeroGalpon = numeroGalpon;
        this.capacidadTotalAves = capacidadTotalAves;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getGranjaId() { return granjaId; }
    public void setGranjaId(UUID granjaId) { this.granjaId = granjaId; }

    public int getNumeroGalpon() { return numeroGalpon; }
    public void setNumeroGalpon(int numeroGalpon) { this.numeroGalpon = numeroGalpon; }

    public int getCapacidadTotalAves() { return capacidadTotalAves; }
    public void setCapacidadTotalAves(int capacidadTotalAves) { this.capacidadTotalAves = capacidadTotalAves; }
}
