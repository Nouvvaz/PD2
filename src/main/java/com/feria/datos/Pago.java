package com.feria.datos;

import java.time.LocalDate;

/**
 * Un pago realizado. {@code valorAcreditado} es lo que cubre del precio base
 * (si hubo descuento por pronto pago, cubre más de lo que se pagó).
 */
public record Pago(double monto, double valorAcreditado, LocalDate fecha) {
}
