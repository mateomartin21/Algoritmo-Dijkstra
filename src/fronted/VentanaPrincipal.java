package fronted;

import core.Nodo;
import core.Grafos;

import java.awt.Color;

import javax.swing.JFrame;
public class VentanaPrincipal extends JFrame {
    public VentanaPrincipal(){
        this.setTitle("DIJKSTRA"); //Titulo
        this.setSize(800, 600); //tamaño ventana
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //Que el programa termine al cerrar
        this.setLocationRelativeTo(null); //Que la ventana aparezca en el centro de la pantalla 

        //usamos BordeLayout para acomodar el lienzo al centro y el boton abajo
        this.setLayout(new java.awt.BorderLayout());
        //prueba
        Grafos miGrafo = new Grafos();

        //Le pasamos el Grafo con datos al lienzo
        PanelJuego lienzo = new PanelJuego(miGrafo);
        this.add(lienzo, java.awt.BorderLayout.CENTER);

    
        //Nuevo botón
        javax.swing.JButton btnModo = new javax.swing.JButton("Jugar ");
        btnModo.setBackground(Color.DARK_GRAY);
        btnModo.setForeground(Color.WHITE);

        btnModo.addActionListener(e -> {
            lienzo.cambiarModo();
            if (lienzo.isModoEdicion()){
                btnModo.setText("Jugar");
            } else {
                btnModo.setText("Editar Mapa");

            }
        });
        this.add(btnModo, java.awt.BorderLayout.SOUTH);

    }   
    
}
