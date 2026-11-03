package com.feria.datos;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/** Almacén en memoria de stands y editoriales (sustituible por H2 más adelante). */
public class Repositorio {
    private final Map<String, Stand> stands = new LinkedHashMap<>();
    private final Map<String, Editorial> editoriales = new LinkedHashMap<>();

    public void guardar(Stand s) { stands.put(s.getId().toUpperCase(), s); }
    public Optional<Stand> buscarStand(String id) { return Optional.ofNullable(stands.get(id.toUpperCase())); }
    public Collection<Stand> todosLosStands() { return stands.values(); }

    public void guardar(Editorial e) { editoriales.put(e.nombre().toLowerCase(), e); }
    public Optional<Editorial> buscarEditorial(String nombre) { return Optional.ofNullable(editoriales.get(nombre.toLowerCase())); }
    public Collection<Editorial> todasLasEditoriales() { return editoriales.values(); }
}
