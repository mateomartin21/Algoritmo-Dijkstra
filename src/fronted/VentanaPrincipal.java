package fronted;

import core.Nodo;
import core.Grafos;
import javax.swing.JFrame;
public class VentanaPrincipal extends JFrame {
    public VentanaPrincipal(){
        this.setTitle("DIJKSTRA"); //Titulo
        this.setSize(800, 600); //tamaño ventana
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //Que el programa termine al cerrar
        this.setLocationRelativeTo(null); //Que la ventana aparezca en el centro de la pantalla 

        //prueba
        Grafos miGrafo = new Grafos();

        //Le pasamos el Grafo con datos al lienzo
        PanelJuego lienzo = new PanelJuego(miGrafo);
        this.add(lienzo);
    }   
    
}
