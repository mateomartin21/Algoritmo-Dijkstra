package core;

public class Arista {
    private Nodo destino;
    private double peso; // El costo del camino (distancia, tráfico, etc.)

    public Arista(Nodo destino, double peso) {
        this.destino = destino;
        this.peso = peso;
    }

    // Getters para que Dijkstra y la interfaz gráfica puedan leer los datos
    public Nodo getDestino() {
        return destino;
    }

    public double getPeso() {
        return peso;
    }
}