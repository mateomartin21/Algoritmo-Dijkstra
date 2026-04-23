package fronted;

import javax.swing.JFrame;
public class VentanaPrincipal extends JFrame {
    public VentanaPrincipal(){
        this.setTitle("DIJKSTRA"); //Titulo
        this.setSize(800, 800); //tamaño ventana
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //Que el programa termine al cerrar
        this.setLocationRelativeTo(null); //Que la ventana aparezca en el centro de la pantalla 

        //poner liento del dibujo a la ventana
        PanelJuego lienzo = new PanelJuego();
        this.add(lienzo);

    }   
    
}
