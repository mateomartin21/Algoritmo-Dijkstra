package fronted;
import core.Nodo;
import core.Arista;

import core.Grafos;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.annotation.Repeatable;

public class PanelJuego extends JPanel{
    private Grafos grafo; //aqui guardamos la logica del juego
    private char letraSiguienteNodo = 'A'; //para que se nombren automaticamente
    private Nodo nodoSeleccionado = null; //para saber que nodo se tocó primero al unir

    public PanelJuego(Grafos grafo){
        this.grafo = grafo;
        this.setBackground(new Color(30,30,30));
        //escuchar clics del ratón
        this.addMouseListener(new MouseAdapter(){
            @Override 
            public void mouseClicked(MouseEvent e){
                int mouseX = e.getX();
                int mouseY = e.getY();
            
            // 1. Revisar si el usuario hizo clic sobre un nodo
            Nodo nodoClickeado = obtenerNodoBajoElRaton(mouseX, mouseY);
            
            if (nodoClickeado != null){
                if (nodoSeleccionado == null){
                    nodoSeleccionado = nodoClickeado;
                } else {
                    //si ya habia uno seleccionado los conectamos
                    //simulamos un peso aleatorio para la arista del 1 al 15
                    double pesoAleatorio = Math.floor(Math.random() * 15) + 1;
                    nodoSeleccionado.agregarArista(nodoClickeado, pesoAleatorio);  
                    //ruta bidireccional
                    nodoClickeado.agregarArista(nodoSeleccionado, pesoAleatorio);
                    nodoSeleccionado = null;
                }
            } else{
                // si hizo clic en el vacio
                String idNuevo = String.valueOf(letraSiguienteNodo);
                Nodo nuevoNodo = new Nodo(idNuevo, mouseX, mouseY);
                PanelJuego.this.grafo.agregarNodo(nuevoNodo);

                letraSiguienteNodo++; //avanza a la siguiente letra del laberinto
                nodoSeleccionado = null; }
                repaint(); //le dije a java que vuelva a dibujar todo el lienzo
        }
        });
    }

    //para saber si el clic cayó dentro del circulo de un nodo 
    private Nodo obtenerNodoBajoElRaton(int mouseX, int mouseY){
            int radio = 20;
            for (Nodo nodo : grafo.getTodosLosNodos()){
                //Teorema de pitagoras para la distancia entre 2 puntos
                double distancia = Math.sqrt(Math.pow(mouseX - nodo.getX(), 2) + Math.pow(mouseY - nodo.getY(), 2));  

                if (distancia <= radio){
                    return nodo;
                }
            }
            return null;
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

    //si el grafo está vacio entonces no digujamos nada
    if (grafo == null || grafo.getTodosLosNodos() == null){
        return;
    }

    //1 - dibujar las aristas (Lineas y pesos)
    for (Nodo origen : grafo.getTodosLosNodos()){
        for (Arista arista : origen.getAdyacentes()){
            Nodo destino = arista.getDestino();

            //dibujar la linea de conexión
            g2d.setColor(Color.WHITE);
            g2d.drawLine(origen.getX(), origen.getY(), destino.getX(), destino.getY());

            //Calcular el punto medi de la inea para poner el numero de "Peso"
            //formula matematica del punto medi (x1 + x2 )/2
            int medioX = (origen.getX() + destino.getX()) / 2;
            int medioY = (origen.getY() + destino.getY()) / 2;

            //dibujar el texto del peso encima de la linea
            g2d.setColor(Color.YELLOW);
            g2d.drawString(String.valueOf(arista.getPeso()), medioX, medioY-5 );

        }
    }

    //2 - dibujar los nodos
    int radio = 20;
    for (Nodo nodo : grafo.getTodosLosNodos()){
        //Dibujar el interior del circulo
        if (nodo == nodoSeleccionado){g2d.setColor(Color.MAGENTA);}
         else { g2d.setColor(Color.CYAN);}
        g2d.fillOval(nodo.getX() - radio, nodo.getY() - radio, radio * 2, radio * 2);
        //Dibujar un borde blanco 
        g2d.setColor(Color.WHITE);
        g2d.drawOval(nodo.getX() - radio, nodo.getY() - radio, radio * 2, radio * 2);

        //Dibujar la ientra (ID) del nodo justo en el centro
        g2d.setColor(Color.BLACK);
        g2d.drawString(nodo.getId(), nodo.getX() - 4, nodo.getY() + 4);

    }
   
    
    }


}



