package core;

import java.util.ArrayList;
import java.util.List;

public class Nodo implements Comparable<Nodo> {
    private String id;
    private int x;
    private int y;
    private double distanciaMinima = Double.POSITIVE_INFINITY;
    private Nodo predecesor;
    private List<Arista> adyacentes;

    public Nodo(String id, int x, int y) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.adyacentes = new ArrayList<>();
    }

    public void agregarArista(Nodo destino, double peso) {
        this.adyacentes.add(new Arista(destino, peso));
    }

    public void resetear() {
        this.distanciaMinima = Double.POSITIVE_INFINITY;
        this.predecesor = null;
    }

    public String getId() { return id; }
    public int getX() { return x; }
    public int getY() { return y; }
    public double getDistanciaMinima() { return distanciaMinima; }
    public Nodo getPredecesor() { return predecesor; }
    public List<Arista> getAdyacentes() { return adyacentes; }

    public void setDistanciaMinima(double distanciaMinima) { this.distanciaMinima = distanciaMinima; }
    public void setPredecesor(Nodo predecesor) { this.predecesor = predecesor; }

    @Override
    public int compareTo(Nodo otro) {
        return Double.compare(this.distanciaMinima, otro.getDistanciaMinima());
    }

    public double obtenerCostoHacia(Nodo destinoDeseado) {
        for (Arista arista : this.adyacentes) {
            if (arista.getDestino() == destinoDeseado) return arista.getPeso();
        }
        return 0.0;
    }

    public boolean tieneConexionCon(Nodo destinoDeseado) {
        for (Arista arista : this.adyacentes) {
            if (arista.getDestino() == destinoDeseado) return true;
        }
        return false;
    }
}
