package com.feria.negocio;

import com.feria.configuracion.Configuracion;
import com.feria.datos.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/** Lógica de negocio: reservas, pagos, bloqueos y transferencias. */
public class ServicioStands {
    private final Repositorio repo;
    private final Configuracion config;

    public ServicioStands(Repositorio repo, Configuracion config) {
        this.repo = repo;
        this.config = config;
    }

    public Stand crearStand(String id, double m2) {
        if (m2 <= 0) throw new ReglaNegocioException("Los metros cuadrados deben ser positivos.");
        if (repo.buscarStand(id).isPresent()) throw new ReglaNegocioException("Ya existe el stand " + id + ".");
        Stand s = new Stand(id.toUpperCase(), m2, m2 * config.getCostoPorM2());
        repo.guardar(s);
        return s;
    }

    /** DISPONIBLE -> RESERVADO (con tiempo de vigencia). */
    public Stand reservar(String id, Editorial editorial, LocalDate hoy) {
        Stand s = obtener(id);
        if (s.getEstado() != EstadoStand.DISPONIBLE)
            throw new ReglaNegocioException("El stand " + s.getId() + " no está disponible (" + s.getEstado() + ").");
        ponerReservado(s, editorial, hoy);
        return s;
    }

    /** RESERVADO u OCUPADO: registra un pago; si alcanza el mínimo pasa a OCUPADO. */
    public Stand pagar(String id, double monto, LocalDate hoy) {
        Stand s = obtener(id);
        if (s.getEstado() != EstadoStand.RESERVADO && s.getEstado() != EstadoStand.OCUPADO)
            throw new ReglaNegocioException("El stand " + s.getId() + " no admite pagos (" + s.getEstado() + ").");
        if (hoy.isAfter(config.getFechaMaximaCubrirCosto()))
            throw new ReglaNegocioException("Ya pasó la fecha máxima para cubrir el costo ("
                    + config.getFechaMaximaCubrirCosto() + ").");
        if (monto <= 0) throw new ReglaNegocioException("El monto debe ser positivo.");

        double factor = factorDescuento(hoy);
        double acreditado = monto / factor;
        double restante = s.getPrecioBase() - s.totalAcreditado();
        if (acreditado > restante + 0.005)
            throw new ReglaNegocioException(String.format("El monto excede lo que falta. Máximo a pagar hoy: $%.2f",
                    restante * factor));

        s.getPagos().add(new Pago(monto, acreditado, hoy));
        if (s.getEstado() == EstadoStand.RESERVADO
                && s.porcentajeCubierto() >= config.getPorcentajeMinimoOcupado()) {
            s.setEstado(EstadoStand.OCUPADO);
            s.setVigenciaReserva(null);
        }
        return s;
    }

    /** DISPONIBLE -> BLOQUEADO (reserva especial del administrador). */
    public Stand bloquear(String id, String empresa) {
        Stand s = obtener(id);
        if (s.getEstado() != EstadoStand.DISPONIBLE)
            throw new ReglaNegocioException("Solo se puede bloquear un stand disponible (" + s.getEstado() + ").");
        s.setEstado(EstadoStand.BLOQUEADO);
        s.setEmpresaBloqueo(empresa);
        return s;
    }

    /** BLOQUEADO -> DISPONIBLE. */
    public Stand liberar(String id) {
        Stand s = obtener(id);
        if (s.getEstado() != EstadoStand.BLOQUEADO)
            throw new ReglaNegocioException("El stand " + s.getId() + " no está bloqueado.");
        s.setEstado(EstadoStand.DISPONIBLE);
        s.setEmpresaBloqueo(null);
        return s;
    }

    /** BLOQUEADO -> RESERVADO directamente a nombre de la editorial (transferencia). */
    public Stand transferir(String id, Editorial editorial, LocalDate hoy) {
        Stand s = obtener(id);
        if (s.getEstado() != EstadoStand.BLOQUEADO)
            throw new ReglaNegocioException("Solo se puede transferir un stand bloqueado.");
        s.setEmpresaBloqueo(null);
        ponerReservado(s, editorial, hoy);
        return s;
    }

    /** Las reservas vencidas que no llegaron al mínimo vuelven a DISPONIBLE. */
    public List<Stand> liberarReservasVencidas(LocalDate hoy) {
        List<Stand> liberados = new ArrayList<>();
        for (Stand s : repo.todosLosStands()) {
            if (s.getEstado() == EstadoStand.RESERVADO && s.getVigenciaReserva().isBefore(hoy)) {
                s.setEstado(EstadoStand.DISPONIBLE);
                s.setEditorial(null);
                s.setVigenciaReserva(null);
                s.getPagos().clear();
                liberados.add(s);
            }
        }
        return liberados;
    }

    private void ponerReservado(Stand s, Editorial editorial, LocalDate hoy) {
        s.setEstado(EstadoStand.RESERVADO);
        s.setEditorial(editorial);
        s.setVigenciaReserva(hoy.plusDays(config.getDiasVigenciaReserva()));
    }

    /** 1.0 sin descuento; (1 - d/100) si se paga a más tardar en la fecha de pronto pago. */
    private double factorDescuento(LocalDate hoy) {
        return hoy.isAfter(config.getFechaProntoPago()) ? 1.0 : 1.0 - config.getDescuentoProntoPago() / 100.0;
    }

    private Stand obtener(String id) {
        return repo.buscarStand(id).orElseThrow(() -> new ReglaNegocioException("No existe el stand " + id + "."));
    }
}
