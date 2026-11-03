package com.feria.notificaciones;

/** Canal de salida de las notificaciones (consola, correo, etc.). */
public interface Notificador {
    void enviar(Notificacion notificacion);
}
