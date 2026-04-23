package core;

import java.util.ArrayList;
import java.util.List;

public class Nodo implements Comparable<Nodo> {
    // --- Atributos visuales y de identificación ---
    private String id;
    private int x; // Coordenada X para que Astrit dibuje el nodo
    private int y; // Coordenada Y para que Astrit dibuje el nodo
    
    // --- Atributos lógicos para el Algoritmo de Dijkstra ---
    // Inicia en "infinito" porque al principio no conocemos la distancia a ningún nodo
    private double distanciaMinima = Double.POSITIVE_INFINITY; 
    private Nodo predecesor; // Guarda de qué nodo venimos (crucial para trazar la ruta final)
    private List<Arista> adyacentes; // Lista de caminos que salen de este nodo

    public Nodo(String id, int x, int y) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.adyacentes = new ArrayList<>();
    }

    // Método para conectar este nodo con otro
    public void agregarArista(Nodo destino, double peso) {
        this.adyacentes.add(new Arista(destino, peso));
    }

    // Método para limpiar el nodo y poder jugar una nueva partida sin reiniciar el programa
    public void resetear() {
        this.distanciaMinima = Double.POSITIVE_INFINITY;
        this.predecesor = null;
    }

    // --- Getters ---
    public String getId() { return id; }
    public int getX() { return x; }
    public int getY() { return y; }
    public double getDistanciaMinima() { return distanciaMinima; }
    public Nodo getPredecesor() { return predecesor; }
    public List<Arista> getAdyacentes() { return adyacentes; }

    // --- Setters (Solo para los atributos que Dijkstra va a modificar) ---
    public void setDistanciaMinima(double distanciaMinima) {
        this.distanciaMinima = distanciaMinima;
    }

    public void setPredecesor(Nodo predecesor) {
        this.predecesor = predecesor;
    }

    // --- El método mágico para la Cola de Prioridad ---
    @Override
    public int compareTo(Nodo otro) {
        // Le dice a Java cómo ordenar los nodos: el que tenga menor distancia va primero
        return Double.compare(this.distanciaMinima, otro.getDistanciaMinima());
    }

    public double obtenerCostoHacia(Nodo destinoDeseado) {
        for (Arista arista : this.adyacentes) {
            if (arista.getDestino() == destinoDeseado) {
                return arista.getPeso();
            }
        }
        return 0.0; 
    }

    public boolean tieneConexionCon(Nodo destinoDeseado) {
        for (Arista arista : this.adyacentes) {
            if (arista.getDestino() == destinoDeseado) {
                return true; // 
            }
        }
        return false; // No están conectados
    }
}
