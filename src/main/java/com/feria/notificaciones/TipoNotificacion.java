package com.feria.notificaciones;

public enum TipoNotificacion {
    RESERVA_POR_VENCER("Su reserva está por vencer"),
    PRONTO_PAGO_POR_VENCER("El descuento por pronto pago está por vencer"),
    FECHA_LIMITE_POR_VENCER("La fecha límite para cubrir el costo está por vencer");

    private final String texto;
    TipoNotificacion(String texto) { this.texto = texto; }
    public String getTexto() { return texto; }
}
