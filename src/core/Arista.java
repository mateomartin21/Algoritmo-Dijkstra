package core;

public class Arista {
    private Nodo destino;
    private double peso;

    public Arista(Nodo destino, double peso) {
        this.destino = destino;
        this.peso = peso;
    }

    public Nodo getDestino() { return destino; }
    public double getPeso() { return peso; }
}