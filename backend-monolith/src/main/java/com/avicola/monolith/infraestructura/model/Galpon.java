package com.avicola.monolith.infraestructura.model;

import jakarta.persistence.*;
import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Table(name = "galpones", uniqueConstraints = {
    @UniqueConstraint(name = "uk_granja_galpon", columnNames = {"granja_id", "numero_galpon"})
})
public class Galpon {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "granja_id", nullable = false)
    private Granja granja;

    @Column(name = "numero_galpon", nullable = false)
    private int numeroGalpon;

    @Column(name = "capacidad_total_aves", nullable = false)
    private int capacidadTotalAves;

    @Column(name = "creado_en", updatable = false)
    private ZonedDateTime creadoEn = ZonedDateTime.now();

    public Galpon() {}

    public Galpon(Granja granja, int numeroGalpon, int capacidadTotalAves) {
        this.granja = granja;
        this.numeroGalpon = numeroGalpon;
        this.capacidadTotalAves = capacidadTotalAves;
        this.creadoEn = ZonedDateTime.now();
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public Granja getGranja() { return granja; }
    public void setGranja(Granja granja) { this.granja = granja; }

    public int getNumeroGalpon() { return numeroGalpon; }
    public void setNumeroGalpon(int numeroGalpon) { this.numeroGalpon = numeroGalpon; }

    public int getCapacidadTotalAves() { return capacidadTotalAves; }
    public void setCapacidadTotalAves(int capacidadTotalAves) { this.capacidadTotalAves = capacidadTotalAves; }

    public ZonedDateTime getCreadoEn() { return creadoEn; }
    public void setCreadoEn(ZonedDateTime creadoEn) { this.creadoEn = creadoEn; }
}
