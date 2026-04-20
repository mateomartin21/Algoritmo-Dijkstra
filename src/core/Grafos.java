package core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

public class Grafos {
    // Usamos un HashMap para poder buscar los nodos rápidamente por su ID (ej. "A", "B")
    private Map<String, Nodo> nodos;

    public Grafos() {
        this.nodos = new HashMap<>();
    }

    // Método para meter un nuevo nodo al mapa
    public void agregarNodo(Nodo nodo) {
        nodos.put(nodo.getId(), nodo);
    }

    public Nodo getNodo(String id) {
        return nodos.get(id);
    }

    // --- EL CEREBRO DEL JUEGO: ALGORITMO DE DIJKSTRA ---
    public void ejecutarDijkstra(String idOrigen) {
        Nodo origen = nodos.get(idOrigen);
        if (origen == null) return;

        // 1. Inicializar la distancia del origen en 0
        origen.setDistanciaMinima(0);

        // 2. Crear la Cola de Prioridad (¡Requisito clave de tu proyecto!)
        PriorityQueue<Nodo> colaPrioridad = new PriorityQueue<>();
        colaPrioridad.add(origen);

        // 3. Ciclo principal de Dijkstra
        while (!colaPrioridad.isEmpty()) {
            // Sacamos el nodo que tiene la distancia más corta (gracias al compareTo que hicimos antes)
            Nodo actual = colaPrioridad.poll();

            // Exploramos todos los caminos (aristas) que salen de este nodo
            for (Arista arista : actual.getAdyacentes()) {
                Nodo vecino = arista.getDestino();
                double pesoCamino = arista.getPeso();

                // Calculamos cuánto nos costaría llegar a este vecino pasando por el nodo actual
                double nuevaDistancia = actual.getDistanciaMinima() + pesoCamino;

                // Si encontramos una ruta MÁS CORTA o BARATA que la que el vecino ya conocía...
                if (nuevaDistancia < vecino.getDistanciaMinima()) {
                    // Quitamos al vecino de la cola (si estaba) para actualizarlo
                    colaPrioridad.remove(vecino); 
                    
                    // Actualizamos sus datos (Relajación)
                    vecino.setDistanciaMinima(nuevaDistancia);
                    vecino.setPredecesor(actual);
                    
                    // Lo volvemos a meter a la cola con su nueva prioridad
                    colaPrioridad.add(vecino);
                }
            }
        }
    }

    // Este método rastrea los predecesores hacia atrás para darte la ruta final exacta
    public List<Nodo> obtenerRutaMasCorta(String idDestino) {
        List<Nodo> ruta = new ArrayList<>();
        Nodo actual = nodos.get(idDestino);

        // Si el destino no tiene predecesor y no es el origen, significa que no hay camino posible
        if (actual == null || (actual.getPredecesor() == null && actual.getDistanciaMinima() != 0)) {
            return ruta; // Retorna lista vacía (no se puede llegar)
        }
    
                // Trazamos el camino hacia atrás
                while (actual != null) {
                    ruta.add(0, actual); // Lo agregamos al inicio de la lista para que quede en orden correcto
                    actual = actual.getPredecesor();
                }
        
                return ruta;
            }
        }