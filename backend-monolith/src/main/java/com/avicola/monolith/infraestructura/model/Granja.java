package com.avicola.monolith.infraestructura.model;

import jakarta.persistence.*;
import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Table(name = "granjas")
public class Granja {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false, length = 200)
    private String ubicacion;

    @Column(name = "creado_en", updatable = false)
    private ZonedDateTime creadoEn = ZonedDateTime.now();

    public Granja() {}

    public Granja(String nombre, String ubicacion) {
        this.nombre = nombre;
        this.ubicacion = ubicacion;
        this.creadoEn = ZonedDateTime.now();
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getUbicacion() { return ubicacion; }
    public void setUbicacion(String ubicacion) { this.ubicacion = ubicacion; }

    public ZonedDateTime getCreadoEn() { return creadoEn; }
    public void setCreadoEn(ZonedDateTime creadoEn) { this.creadoEn = creadoEn; }
}
