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

    
        //panel para poner varios botones
        javax.swing.JPanel panelBotones = new javax.swing.JPanel();
        panelBotones.setBackground(Color.BLACK);

        //Nuevo botón
        javax.swing.JButton btnModo = new javax.swing.JButton("Jugar ");
        btnModo.addActionListener(e -> {
            lienzo.cambiarModo();
            if (lienzo.isModoEdicion()){
                btnModo.setText("Jugar");
            } else {
                btnModo.setText("Editar Mapa");
            }
            });

        //boton IA
        javax.swing.JButton btnIA = new javax.swing.JButton("Terminar para correr IA ");
        btnIA.setBackground(Color.RED);
        btnIA.setForeground(Color.WHITE); // (Opcional, para que la letra se vea mejor)
        btnIA.addActionListener(e -> {
            // Solo dejamos que corra la IA si YA estamos en modo juego
            if (!lienzo.isModoEdicion()) {
                lienzo.iniciarCarreraIA(); 
            }
        });

        // Boton para reiniciar
        javax.swing.JButton btnReiniciar = new javax.swing.JButton("Reiniciar partida ");
        btnReiniciar.setBackground(Color.DARK_GRAY);
        btnReiniciar.setForeground(Color.WHITE); // (Opcional, para que la letra se vea mejor)
        btnReiniciar.addActionListener(e -> {
            // Solo dejamos que corra la IA si YA estamos en modo juego
            if (!lienzo.isModoEdicion()) {
                lienzo.reiniciarPartidad();
            }
        });


        panelBotones.add(btnModo);
        panelBotones.add(btnIA);
         panelBotones.add(btnReiniciar);
        this.add(panelBotones, java.awt.BorderLayout.SOUTH);

        

    }   
    
}
