package fronted;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

public class PanelJuego extends JPanel{
    public PanelJuego(){
        this.setBackground(new Color(30,30,30));
    }

    //paintComponet es llamado automaticamente por java cada vez que la pantalla necesita dibujarse o au¿ctualizarse
    @Override
    protected void paintComponent(Graphics g){
        super.paintComponent(g); //limpia el panrl antes de redibujar
    
    //converierte el pinceo "g" a "Graphics2D" porque da mejoras
    //herramientas para dibujar
    Graphics2D g2d = (Graphics2D) g;

    //activamos el "Antialiasing" para que los circulos y lineas no se vean pixelados
    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    //prueba: dibujar un nodo manual 
    // Elegimos el color del pincel
    g2d.setColor(Color.CYAN);
    
    // Dibujamos un circulo relleno
    //parametros: (posicinX, posicionY, ancho, alto)
    int radio = 20;
    g2d.fillOval(100 -radio, 100 - radio, radio * 2, radio * 2);

    //dibujamos el ID del nodo en texto blanco
    g2d.setColor(Color.WHITE);
    g2d.drawString("A", 100 - 5, 100 + 5);

    }


}



