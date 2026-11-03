package com.feria.notificaciones;

import com.feria.configuracion.Configuracion;
import com.feria.datos.EstadoStand;
import com.feria.datos.Repositorio;
import com.feria.datos.Stand;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/** Revisa los vencimientos y avisa a las editoriales. */
public class ServicioNotificaciones {
    private final Repositorio repo;
    private final Configuracion config;
    private final Notificador notificador;

    public ServicioNotificaciones(Repositorio repo, Configuracion config, Notificador notificador) {
        this.repo = repo;
        this.config = config;
        this.notificador = notificador;
    }

    /** Genera y envía las notificaciones que corresponden a la fecha dada. */
    public List<Notificacion> revisar(LocalDate hoy) {
        List<Notificacion> enviadas = new ArrayList<>();
        for (Stand s : repo.todosLosStands()) {
            if (s.getEditorial() == null) continue;

            if (s.getEstado() == EstadoStand.RESERVADO) {
                agregar(enviadas, s, TipoNotificacion.RESERVA_POR_VENCER, s.getVigenciaReserva(), hoy);
            }
            boolean conSaldo = (s.getEstado() == EstadoStand.RESERVADO || s.getEstado() == EstadoStand.OCUPADO)
                    && !s.estaCubiertoCompleto();
            if (conSaldo) {
                agregar(enviadas, s, TipoNotificacion.PRONTO_PAGO_POR_VENCER, config.getFechaProntoPago(), hoy);
                agregar(enviadas, s, TipoNotificacion.FECHA_LIMITE_POR_VENCER, config.getFechaMaximaCubrirCosto(), hoy);
            }
        }
        enviadas.forEach(notificador::enviar);
        return enviadas;
    }

    private void agregar(List<Notificacion> lista, Stand s, TipoNotificacion tipo, LocalDate vence, LocalDate hoy) {
        long dias = ChronoUnit.DAYS.between(hoy, vence);
        if (dias >= 0 && dias <= config.getDiasAnticipoAviso()) {
            lista.add(new Notificacion(s.getEditorial(), tipo, s.getId(), vence, dias));
        }
    }
}
