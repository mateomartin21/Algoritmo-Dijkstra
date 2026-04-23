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
import javax.swing.Timer;

public class PanelJuego extends JPanel{
    private Grafos grafo; //aqui guardamos la logica del juego
    private char letraSiguienteNodo = 'A'; //para que se nombren automaticamente
    private Nodo nodoSeleccionado = null; //para saber que nodo se tocó primero al unir
    private String mensajePantalla = "Modo Edicion: Haz clic para crear o unir nodos." ;
    private double costoPersona = 0.0;
    private boolean modoEdicion = true;
    private Nodo nodoJugador = null;
    private Nodo nodoInicial = null; //para recordar donde empezó la carrera

    //variables para IA
    private Nodo nodoIA = null;
    private double costoIA = 0.0;
    private java.util.List<Nodo> rutaIA = null;
    private int pasoAnimacionIA = 0;


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
           
            if (modoEdicion) {
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
                PanelJuego.this.grafo.agregarNodo(new Nodo(idNuevo, mouseX, mouseY));

                letraSiguienteNodo++; //avanza a la siguiente letra del laberinto
                nodoSeleccionado = null; }
            } else {
                if (nodoClickeado != null){
                    if(nodoJugador == null){
                        //primer clic el usuario elige donde empezar
                        nodoJugador = nodoClickeado;
                        nodoInicial =nodoClickeado;
                        costoPersona = 0.0;
                        mensajePantalla = "Iniciaste en el nodo: " + nodoJugador.getId() + ". ¡Elige tu siguiente paso!";
                    } else {
                        if (nodoJugador.tieneConexionCon(nodoClickeado)){
                            //se cumple la validacion
                            double costoDelPaso = nodoJugador.obtenerCostoHacia(nodoClickeado);
                            costoPersona = costoDelPaso + costoPersona;

                            nodoJugador = nodoClickeado;
                            mensajePantalla = "Te moviste al nodo: " + nodoJugador.getId()+ " (Costo: +" + costoDelPaso + ")";
                        } else {
                            //falla la validacion
                            mensajePantalla = "¡Movimiento invalido! No puedes saltar hasta ahi." + nodoClickeado.getId();
                        }
                    } 
                }
            }
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
    // 3 - dibujar al jugador
    if (!modoEdicion && nodoJugador != null){
        g2d.setColor(Color.GREEN); //el jugador es de color verde
        int radioJugador = 10; //mas chico que el nodo para que entre dentro
        g2d.fillOval(nodoJugador.getX()- radioJugador, nodoJugador.getY() -radioJugador, radioJugador*2, radioJugador*2);
    }

        g2d.setColor(Color.WHITE);
        g2d.setFont(new java.awt.Font("SansSerif", java.awt.Font.BOLD, 16));
        g2d.drawString("Mensaje: " + mensajePantalla, 20, 30);

        if (!modoEdicion) {
            g2d.setColor(Color.ORANGE);
            g2d.drawString("Costo Humano: " + costoPersona, 20, 60);
        }

        //dibujar datos de la IA
        if (!modoEdicion ){
        g2d.setColor(Color.PINK); //el jugador es de color verde
        g2d.drawString ("Costo IA (Dijkstra): " + costoIA, 20, 90);
    }
        //dibujar la ficha de la IA (en rojo)
        if (nodoIA != null){
            g2d.setColor(Color.RED);
            int radioIA = 8;
            //lo digujamos un poco movido para que si el usuario y la IA esten en el mismo nodo entonces no se tapen las fichas
            g2d.fillOval(nodoIA.getX()- radioIA, nodoIA.getY() -radioIA, radioIA*2, radioIA*2);
        }


   
    
    }

    public void cambiarModo(){
        this.modoEdicion = !this.modoEdicion;
        this.nodoSeleccionado = null;
        repaint();
    }

    public boolean isModoEdicion() {
        return this.modoEdicion;
    }

    //Metodo que desata a la IA
    public void iniciarCarreraIA(){
    //VALIDAMOS QUE EL JUGADOR YA JUGÓ
        if (nodoInicial == null || nodoJugador == null){
            mensajePantalla = "¡Primero debes jugar tú y llegar a un destino!";
            repaint();
            return;
        } 
        // 1 -limpiamos datos viejos que del grafo antes de calcular
        for (Nodo n : grafo.getTodosLosNodos()){
            n.resetear();
        }
        //2 - ejecutamos el algoritmo desde donde empezaste
        grafo.ejecutarDijkstra(nodoInicial.getId());

        // 3 - le pedimos a Dikjstra la ruta perfecta hasta donde termine el usuario
        rutaIA = grafo.obtenerRutaMasCorta(nodoJugador.getId());
        if (rutaIA.isEmpty()){
            mensajePantalla = "Dijkstra dice: No hay camino posible hacia allá.";
            repaint();
            return;
        }
        //Preparamos a la IA en la linea de salida
        pasoAnimacionIA = 0;
        costoIA = 0.0;
        nodoIA = rutaIA.get(0);
        mensajePantalla = "¡La IA está calculando y moviéndose!";

        // 5 - Un timer que se ejecuta cada 1 segundo (1000 mls)
        javax.swing.Timer timerAnimacion = new javax.swing.Timer(1000, e ->{
            pasoAnimacionIA++; //avanzamos un paso a la lista
            if (pasoAnimacionIA < rutaIA.size()){
                //nos movemos al siguiente nodo
                Nodo siguienteNodo = rutaIA.get(pasoAnimacionIA);
                costoIA = costoIA + nodoIA.obtenerCostoHacia(siguienteNodo);
                nodoIA = siguienteNodo;
                repaint();
            } else{
                // ya llegó a la meta, obtenemos el Timer
                ((javax.swing.Timer) e.getSource()).stop();

                //Juzgamos quien ganó
                if (costoIA < costoPersona){
                    mensajePantalla = "¡GANA LA IA! Encontró una ruta más barata.";
                }
                else if (costoIA == costoPersona){
                    mensajePantalla = "¡EMPATE! Encontraste la ruta perfecta.";
                }
                else{mensajePantalla = "¡GANASTE TÚ! (Aunque esto es matemáticamente imposible si Dijkstra está bien programado)";
                }
                repaint();
            }
        } );
        timerAnimacion.start(); //inicia el cronometro

}

 }



