package com.feria.configuracion;

import java.time.LocalDate;

/** Parámetros del sistema: costos y plazos. */
public class Configuracion {
    private double costoPorM2;
    private int diasVigenciaReserva;
    private double porcentajeMinimoOcupado;   // % mínimo para pasar de RESERVADO a OCUPADO
    private double descuentoProntoPago;       // % de descuento si se paga a tiempo
    private LocalDate fechaProntoPago;        // último día con descuento
    private LocalDate fechaMaximaCubrirCosto; // último día para cubrir el 100%
    private int diasAnticipoAviso;            // con cuántos días de anticipo se notifica

    public Configuracion(double costoPorM2, int diasVigenciaReserva, double porcentajeMinimoOcupado,
                         double descuentoProntoPago, LocalDate fechaProntoPago,
                         LocalDate fechaMaximaCubrirCosto, int diasAnticipoAviso) {
        this.costoPorM2 = costoPorM2;
        this.diasVigenciaReserva = diasVigenciaReserva;
        this.porcentajeMinimoOcupado = porcentajeMinimoOcupado;
        this.descuentoProntoPago = descuentoProntoPago;
        this.fechaProntoPago = fechaProntoPago;
        this.fechaMaximaCubrirCosto = fechaMaximaCubrirCosto;
        this.diasAnticipoAviso = diasAnticipoAviso;
    }

    public double getCostoPorM2() { return costoPorM2; }
    public void setCostoPorM2(double v) { this.costoPorM2 = v; }
    public int getDiasVigenciaReserva() { return diasVigenciaReserva; }
    public void setDiasVigenciaReserva(int v) { this.diasVigenciaReserva = v; }
    public double getPorcentajeMinimoOcupado() { return porcentajeMinimoOcupado; }
    public void setPorcentajeMinimoOcupado(double v) { this.porcentajeMinimoOcupado = v; }
    public double getDescuentoProntoPago() { return descuentoProntoPago; }
    public void setDescuentoProntoPago(double v) { this.descuentoProntoPago = v; }
    public LocalDate getFechaProntoPago() { return fechaProntoPago; }
    public void setFechaProntoPago(LocalDate v) { this.fechaProntoPago = v; }
    public LocalDate getFechaMaximaCubrirCosto() { return fechaMaximaCubrirCosto; }
    public void setFechaMaximaCubrirCosto(LocalDate v) { this.fechaMaximaCubrirCosto = v; }
    public int getDiasAnticipoAviso() { return diasAnticipoAviso; }
    public void setDiasAnticipoAviso(int v) { this.diasAnticipoAviso = v; }

    @Override
    public String toString() {
        return String.format("""
                Configuración
                  costo por m2:            $%.2f
                  vigencia de reserva:     %d días
                  % mínimo para ocupar:    %.1f%%
                  descuento pronto pago:   %.1f%%
                  fecha pronto pago:       %s
                  fecha máxima de pago:    %s
                  días de anticipo avisos: %d""",
                costoPorM2, diasVigenciaReserva, porcentajeMinimoOcupado, descuentoProntoPago,
                fechaProntoPago, fechaMaximaCubrirCosto, diasAnticipoAviso);
    }
}
