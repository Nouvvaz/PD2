package com.feria.notificaciones;

public class NotificadorConsola implements Notificador {
    @Override
    public void enviar(Notificacion n) {
        System.out.println("[NOTIFICACIÓN] " + n.mensaje());
    }
}
