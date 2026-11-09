package com.avicola.monolith.alertas.dto;

import com.avicola.monolith.alertas.model.AlertaLote;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.UUID;

public class AlertaResponseDTO {
    private UUID id;
    private UUID loteId;
    private String codigoLote;
    private int numeroGalpon;
    private int posicionEnGalpon;
    private String tipoAlerta;
    private String nivelSeveridad;
    private String mensaje;
    private BigDecimal valorRegistrado;
    private BigDecimal valorUmbralEsperado;
    private boolean revisada;
    private ZonedDateTime fechaHoraAlerta;

    public AlertaResponseDTO() {}

    public static AlertaResponseDTO fromEntity(AlertaLote alerta) {
        AlertaResponseDTO dto = new AlertaResponseDTO();
        dto.setId(alerta.getId());
        dto.setLoteId(alerta.getLote().getId());
        dto.setCodigoLote(alerta.getLote().getCodigoLote());
        dto.setNumeroGalpon(alerta.getLote().getGalpon().getNumeroGalpon());
        dto.setPosicionEnGalpon(alerta.getLote().getPosicionEnGalpon());
        dto.setTipoAlerta(alerta.getTipoAlerta().name());
        dto.setNivelSeveridad(alerta.getNivelSeveridad().name());
        dto.setMensaje(alerta.getMensaje());
        dto.setValorRegistrado(alerta.getValorRegistrado());
        dto.setValorUmbralEsperado(alerta.getValorUmbralEsperado());
        dto.setRevisada(alerta.isRevisada());
        dto.setFechaHoraAlerta(alerta.getFechaHoraAlerta());
        return dto;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getLoteId() { return loteId; }
    public void setLoteId(UUID loteId) { this.loteId = loteId; }

    public String getCodigoLote() { return codigoLote; }
    public void setCodigoLote(String codigoLote) { this.codigoLote = codigoLote; }

    public int getNumeroGalpon() { return numeroGalpon; }
    public void setNumeroGalpon(int numeroGalpon) { this.numeroGalpon = numeroGalpon; }

    public int getPosicionEnGalpon() { return posicionEnGalpon; }
    public void setPosicionEnGalpon(int posicionEnGalpon) { this.posicionEnGalpon = posicionEnGalpon; }

    public String getTipoAlerta() { return tipoAlerta; }
    public void setTipoAlerta(String tipoAlerta) { this.tipoAlerta = tipoAlerta; }

    public String getNivelSeveridad() { return nivelSeveridad; }
    public void setNivelSeveridad(String nivelSeveridad) { this.nivelSeveridad = nivelSeveridad; }

    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }

    public BigDecimal getValorRegistrado() { return valorRegistrado; }
    public void setValorRegistrado(BigDecimal valorRegistrado) { this.valorRegistrado = valorRegistrado; }

    public BigDecimal getValorUmbralEsperado() { return valorUmbralEsperado; }
    public void setValorUmbralEsperado(BigDecimal valorUmbralEsperado) { this.valorUmbralEsperado = valorUmbralEsperado; }

    public boolean isRevisada() { return revisada; }
    public void setRevisada(boolean revisada) { this.revisada = revisada; }

    public ZonedDateTime getFechaHoraAlerta() { return fechaHoraAlerta; }
    public void setFechaHoraAlerta(ZonedDateTime fechaHoraAlerta) { this.fechaHoraAlerta = fechaHoraAlerta; }
}
