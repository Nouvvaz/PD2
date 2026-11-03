package com.feria.datos;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Stand {
    private final String id;
    private final double metrosCuadrados;
    private final double precioBase;
    private EstadoStand estado = EstadoStand.DISPONIBLE;
    private Editorial editorial;        // quien reservó u ocupa el stand
    private String empresaBloqueo;      // empresa para la que el admin lo bloqueó
    private LocalDate vigenciaReserva;  // solo si está RESERVADO
    private final List<Pago> pagos = new ArrayList<>();

    public Stand(String id, double metrosCuadrados, double precioBase) {
        this.id = id;
        this.metrosCuadrados = metrosCuadrados;
        this.precioBase = precioBase;
    }

    public double totalAcreditado() {
        return pagos.stream().mapToDouble(Pago::valorAcreditado).sum();
    }

    public double totalPagado() {
        return pagos.stream().mapToDouble(Pago::monto).sum();
    }

    /** Porcentaje del precio base ya cubierto (0 a 100). */
    public double porcentajeCubierto() {
        return totalAcreditado() / precioBase * 100.0;
    }

    public boolean estaCubiertoCompleto() {
        return porcentajeCubierto() >= 99.999;
    }

    public String getId() { return id; }
    public double getMetrosCuadrados() { return metrosCuadrados; }
    public double getPrecioBase() { return precioBase; }
    public EstadoStand getEstado() { return estado; }
    public void setEstado(EstadoStand estado) { this.estado = estado; }
    public Editorial getEditorial() { return editorial; }
    public void setEditorial(Editorial editorial) { this.editorial = editorial; }
    public String getEmpresaBloqueo() { return empresaBloqueo; }
    public void setEmpresaBloqueo(String empresaBloqueo) { this.empresaBloqueo = empresaBloqueo; }
    public LocalDate getVigenciaReserva() { return vigenciaReserva; }
    public void setVigenciaReserva(LocalDate vigenciaReserva) { this.vigenciaReserva = vigenciaReserva; }
    public List<Pago> getPagos() { return pagos; }

    @Override
    public String toString() {
        String detalle = switch (estado) {
            case RESERVADO -> " | " + editorial.nombre() + " | vigencia hasta " + vigenciaReserva
                    + String.format(" | cubierto %.1f%%", porcentajeCubierto());
            case OCUPADO -> " | " + editorial.nombre() + String.format(" | cubierto %.1f%%", porcentajeCubierto());
            case BLOQUEADO -> " | para " + empresaBloqueo;
            case DISPONIBLE -> "";
        };
        return String.format("%-4s %6.1f m2  $%,10.2f  %-10s%s", id, metrosCuadrados, precioBase, estado, detalle);
    }
}
