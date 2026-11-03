package com.feria.app;

import com.feria.configuracion.Configuracion;
import com.feria.datos.Editorial;
import com.feria.datos.Repositorio;
import com.feria.datos.Stand;
import com.feria.negocio.ReglaNegocioException;
import com.feria.negocio.ServicioStands;
import com.feria.notificaciones.NotificadorConsola;
import com.feria.notificaciones.ServicioNotificaciones;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

/** Interfaz de línea de comandos del sistema de venta de stands. */
public class Main {
    private static LocalDate hoy = LocalDate.now();

    public static void main(String[] args) {
        Configuracion config = new Configuracion(1500, 7, 30, 10,
                hoy.plusDays(30), hoy.plusDays(60), 3);
        Repositorio repo = new Repositorio();
        ServicioStands stands = new ServicioStands(repo, config);
        ServicioNotificaciones notificaciones =
                new ServicioNotificaciones(repo, config, new NotificadorConsola());

        sembrarDatos(repo, stands);
        System.out.println("Sistema de venta de stands. Escribe 'ayuda' para ver los comandos.");
        System.out.println("Fecha del sistema: " + hoy);

        Scanner in = new Scanner(System.in);
        while (true) {
            System.out.print("> ");
            if (!in.hasNextLine()) break;
            String[] p = in.nextLine().trim().split("\\s+");
            if (p[0].isEmpty()) continue;
            try {
                switch (p[0].toLowerCase()) {
                    case "ayuda" -> ayuda();
                    case "listar" -> repo.todosLosStands().forEach(System.out::println);
                    case "editoriales" -> repo.todasLasEditoriales()
                            .forEach(e -> System.out.println(e.nombre() + " <" + e.correo() + ">"));
                    case "editorial" -> {
                        requiere(p, 3, "editorial <nombre> <correo>");
                        repo.guardar(new Editorial(p[1], p[2]));
                        System.out.println("Editorial registrada.");
                    }
                    case "reservar" -> {
                        requiere(p, 3, "reservar <idStand> <editorial>");
                        Stand s = stands.reservar(p[1], editorial(repo, p[2]), hoy);
                        System.out.println("Reservado: " + s);
                    }
                    case "pagar" -> {
                        requiere(p, 3, "pagar <idStand> <monto>");
                        Stand s = stands.pagar(p[1], Double.parseDouble(p[2]), hoy);
                        System.out.println("Pago registrado: " + s);
                    }
                    case "bloquear" -> {
                        requiere(p, 3, "bloquear <idStand> <empresa>");
                        System.out.println("Bloqueado: " + stands.bloquear(p[1], p[2]));
                    }
                    case "liberar" -> {
                        requiere(p, 2, "liberar <idStand>");
                        System.out.println("Liberado: " + stands.liberar(p[1]));
                    }
                    case "transferir" -> {
                        requiere(p, 3, "transferir <idStand> <editorial>");
                        System.out.println("Transferido: " + stands.transferir(p[1], editorial(repo, p[2]), hoy));
                    }
                    case "config" -> System.out.println(config);
                    case "fecha" -> {
                        if (p.length > 1) hoy = LocalDate.parse(p[1]);
                        System.out.println("Fecha del sistema: " + hoy);
                    }
                    case "notificar" -> {
                        stands.liberarReservasVencidas(hoy)
                                .forEach(s -> System.out.println("Reserva vencida, stand liberado: " + s.getId()));
                        int n = notificaciones.revisar(hoy).size();
                        System.out.println(n + " notificación(es) enviada(s).");
                    }
                    case "salir" -> { return; }
                    default -> System.out.println("Comando desconocido. Usa 'ayuda'.");
                }
            } catch (ReglaNegocioException e) {
                System.out.println("Error: " + e.getMessage());
            } catch (NumberFormatException e) {
                System.out.println("Error: monto inválido.");
            } catch (DateTimeParseException e) {
                System.out.println("Error: usa el formato AAAA-MM-DD.");
            }
        }
    }

    private static Editorial editorial(Repositorio repo, String nombre) {
        return repo.buscarEditorial(nombre).orElseThrow(() ->
                new ReglaNegocioException("No existe la editorial '" + nombre + "'. Regístrala con 'editorial'."));
    }

    private static void requiere(String[] p, int n, String uso) {
        if (p.length < n) throw new ReglaNegocioException("Uso: " + uso);
    }

    private static void sembrarDatos(Repositorio repo, ServicioStands stands) {
        String[] ids = {"A1", "A2", "A3", "B1", "B2", "B3"};
        double[] m2 = {9, 9, 12, 16, 16, 20};
        for (int i = 0; i < ids.length; i++) stands.crearStand(ids[i], m2[i]);
        repo.guardar(new Editorial("Planeta", "contacto@planeta.test"));
        repo.guardar(new Editorial("Porrua", "ventas@porrua.test"));
    }

    private static void ayuda() {
        System.out.println("""
                listar                         muestra todos los stands
                editoriales                    lista editoriales registradas
                editorial <nombre> <correo>    registra una editorial
                reservar <id> <editorial>      DISPONIBLE -> RESERVADO
                pagar <id> <monto>             registra un pago (RESERVADO/OCUPADO)
                bloquear <id> <empresa>        DISPONIBLE -> BLOQUEADO (admin)
                liberar <id>                   BLOQUEADO -> DISPONIBLE (admin)
                transferir <id> <editorial>    BLOQUEADO -> RESERVADO para la editorial
                config                         muestra la configuración
                fecha [AAAA-MM-DD]             ver o cambiar la fecha del sistema (para pruebas)
                notificar                      libera reservas vencidas y envía avisos
                salir""");
    }
}
