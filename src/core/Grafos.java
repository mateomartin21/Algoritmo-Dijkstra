package core;

import java.util.*;

public class Grafos {
    private Map<String, Nodo> nodos;

    // Para el modo paso a paso
    public static class PasoDijkstra {
        public String nodoActual;
        public String vecino;
        public double distanciaAnterior;
        public double nuevaDistancia;
        public boolean mejorado;
        public String descripcion;

        public PasoDijkstra(String nodoActual, String vecino, double distanciaAnterior,
                            double nuevaDistancia, boolean mejorado) {
            this.nodoActual = nodoActual;
            this.vecino = vecino;
            this.distanciaAnterior = distanciaAnterior;
            this.nuevaDistancia = nuevaDistancia;
            this.mejorado = mejorado;

            if (mejorado) {
                this.descripcion = String.format(
                    "✅ Nodo '%s': dist anterior=%.1f, nueva dist=%.1f (MEJORADO via '%s')",
                    vecino,
                    distanciaAnterior == Double.POSITIVE_INFINITY ? 99999 : distanciaAnterior,
                    nuevaDistancia, nodoActual
                );
            } else {
                this.descripcion = String.format(
                    "❌ Nodo '%s': dist actual=%.1f, nueva propuesta=%.1f (NO mejora)",
                    vecino,
                    distanciaAnterior == Double.POSITIVE_INFINITY ? 99999 : distanciaAnterior,
                    nuevaDistancia
                );
            }
        }
    }

    public Grafos() {
        this.nodos = new LinkedHashMap<>();
    }

    public void agregarNodo(Nodo nodo) {
        nodos.put(nodo.getId(), nodo);
    }

    public void eliminarNodo(String id) {
        Nodo aEliminar = nodos.get(id);
        if (aEliminar == null) return;
        // Eliminar aristas que apunten a este nodo
        for (Nodo n : nodos.values()) {
            n.getAdyacentes().removeIf(a -> a.getDestino() == aEliminar);
        }
        nodos.remove(id);
    }

    public Nodo getNodo(String id) {
        return nodos.get(id);
    }

    public Collection<Nodo> getTodosLosNodos() {
        return nodos.values();
    }

    public void limpiarTodo() {
        nodos.clear();
    }

    // ═══════════════════════════════════════════════════
    // DIJKSTRA NORMAL
    // ═══════════════════════════════════════════════════
    public void ejecutarDijkstra(String idOrigen) {
        Nodo origen = nodos.get(idOrigen);
        if (origen == null) return;

        origen.setDistanciaMinima(0);
        PriorityQueue<Nodo> cola = new PriorityQueue<>();
        cola.add(origen);

        while (!cola.isEmpty()) {
            Nodo actual = cola.poll();
            for (Arista arista : actual.getAdyacentes()) {
                Nodo vecino = arista.getDestino();
                double nuevaDist = actual.getDistanciaMinima() + arista.getPeso();
                if (nuevaDist < vecino.getDistanciaMinima()) {
                    cola.remove(vecino);
                    vecino.setDistanciaMinima(nuevaDist);
                    vecino.setPredecesor(actual);
                    cola.add(vecino);
                }
            }
        }
    }

    // ═══════════════════════════════════════════════════
    // DIJKSTRA PASO A PASO (NUEVA FUNCIÓN)
    // ═══════════════════════════════════════════════════
    public List<PasoDijkstra> ejecutarDijkstraPasoAPaso(String idOrigen) {
        List<PasoDijkstra> pasos = new ArrayList<>();

        Nodo origen = nodos.get(idOrigen);
        if (origen == null) return pasos;

        origen.setDistanciaMinima(0);
        PriorityQueue<Nodo> cola = new PriorityQueue<>();
        cola.add(origen);

        while (!cola.isEmpty()) {
            Nodo actual = cola.poll();
            for (Arista arista : actual.getAdyacentes()) {
                Nodo vecino = arista.getDestino();
                double distAnterior = vecino.getDistanciaMinima();
                double nuevaDist = actual.getDistanciaMinima() + arista.getPeso();
                boolean mejorado = nuevaDist < distAnterior;

                pasos.add(new PasoDijkstra(actual.getId(), vecino.getId(), distAnterior, nuevaDist, mejorado));

                if (mejorado) {
                    cola.remove(vecino);
                    vecino.setDistanciaMinima(nuevaDist);
                    vecino.setPredecesor(actual);
                    cola.add(vecino);
                }
            }
        }
        return pasos;
    }

    // ═══════════════════════════════════════════════════
    // OBTENER RUTA
    // ═══════════════════════════════════════════════════
    public List<Nodo> obtenerRutaMasCorta(String idDestino) {
        List<Nodo> ruta = new ArrayList<>();
        Nodo actual = nodos.get(idDestino);

        if (actual == null || (actual.getPredecesor() == null && actual.getDistanciaMinima() != 0)) {
            return ruta;
        }

        while (actual != null) {
            ruta.add(0, actual);
            actual = actual.getPredecesor();
        }
        return ruta;
    }

    // ═══════════════════════════════════════════════════
    // EXPORTAR A TEXTO (NUEVA FUNCIÓN)
    // ═══════════════════════════════════════════════════
    public String exportarComoTexto() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== GRAFO EXPORTADO ===\n");
        sb.append("Nodos: ").append(nodos.size()).append("\n\n");

        for (Nodo n : nodos.values()) {
            sb.append("NODO ").append(n.getId())
              .append(" (x=").append(n.getX()).append(", y=").append(n.getY()).append(")\n");
            for (Arista a : n.getAdyacentes()) {
                sb.append("  -> ").append(a.getDestino().getId())
                  .append(" [peso=").append(a.getPeso()).append("]\n");
            }
        }
        return sb.toString();
    }

    // ═══════════════════════════════════════════════════
    // ESTADÍSTICAS (NUEVA FUNCIÓN)
    // ═══════════════════════════════════════════════════
    public String obtenerEstadisticas() {
        int totalAristas = 0;
        double pesoMin = Double.MAX_VALUE, pesoMax = 0, pesoSum = 0;
        int contAristas = 0;

        for (Nodo n : nodos.values()) {
            for (Arista a : n.getAdyacentes()) {
                totalAristas++;
                pesoMin = Math.min(pesoMin, a.getPeso());
                pesoMax = Math.max(pesoMax, a.getPeso());
                pesoSum += a.getPeso();
                contAristas++;
            }
        }

        double promedio = contAristas > 0 ? pesoSum / contAristas : 0;

        return String.format(
            "Nodos: %d | Aristas: %d | Peso mín: %.1f | Peso máx: %.1f | Peso prom: %.1f",
            nodos.size(), totalAristas / 2, pesoMin == Double.MAX_VALUE ? 0 : pesoMin, pesoMax, promedio
        );
    }

    // ═══════════════════════════════════════════════════
    // CARGAR GRAFOS PREDEFINIDOS (NUEVA FUNCIÓN)
    // ═══════════════════════════════════════════════════
    public void cargarMapaCiudad(int anchoPanel, int altoPanel) {
        limpiarTodo();
        int mx = anchoPanel / 2;
        int my = altoPanel / 2;

        Nodo cdmx   = new Nodo("CDMX",   mx,       my);
        Nodo puebla = new Nodo("Puebla",  mx+180,   my+80);
        Nodo qro    = new Nodo("Querétaro", mx-160, my-100);
        Nodo gto    = new Nodo("Guanajuato", mx-200, my+50);
        Nodo tol    = new Nodo("Toluca",  mx-120,   my+60);
        Nodo morelia= new Nodo("Morelia", mx-180,   my+160);
        Nodo cuernavaca = new Nodo("Cuernavaca", mx+20, my+130);
        Nodo tlaxcala   = new Nodo("Tlaxcala",   mx+120, my-20);

        agregarNodo(cdmx); agregarNodo(puebla); agregarNodo(qro);
        agregarNodo(gto); agregarNodo(tol); agregarNodo(morelia);
        agregarNodo(cuernavaca); agregarNodo(tlaxcala);

        conectar(cdmx, puebla, 130);   conectar(cdmx, qro, 215);
        conectar(cdmx, tol, 65);       conectar(cdmx, cuernavaca, 85);
        conectar(cdmx, tlaxcala, 120); conectar(puebla, tlaxcala, 30);
        conectar(qro, gto, 100);       conectar(tol, morelia, 200);
        conectar(gto, morelia, 100);   conectar(morelia, cuernavaca, 310);
    }

    public void cargarRedServidor(int anchoPanel, int altoPanel) {
        limpiarTodo();
        int mx = anchoPanel / 2;
        int my = altoPanel / 2;

        Nodo s1 = new Nodo("Server-1", mx,      my-150);
        Nodo s2 = new Nodo("Server-2", mx+150,  my-50);
        Nodo s3 = new Nodo("Server-3", mx+150,  my+100);
        Nodo s4 = new Nodo("Server-4", mx,      my+180);
        Nodo s5 = new Nodo("Server-5", mx-150,  my+100);
        Nodo s6 = new Nodo("Server-6", mx-150,  my-50);
        Nodo hub = new Nodo("HUB",     mx,      my);

        agregarNodo(s1); agregarNodo(s2); agregarNodo(s3);
        agregarNodo(s4); agregarNodo(s5); agregarNodo(s6); agregarNodo(hub);

        conectar(s1, hub, 5); conectar(s2, hub, 3); conectar(s3, hub, 7);
        conectar(s4, hub, 4); conectar(s5, hub, 6); conectar(s6, hub, 2);
        conectar(s1, s2, 12); conectar(s2, s3, 8);  conectar(s3, s4, 10);
        conectar(s4, s5, 9);  conectar(s5, s6, 11); conectar(s6, s1, 7);
    }

    private void conectar(Nodo a, Nodo b, double peso) {
        a.agregarArista(b, peso);
        b.agregarArista(a, peso);
    }
}