package fronted;

import core.Arista;
import core.Grafos;
import core.Nodo;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.List;

public class PanelJuego extends JPanel {

    // ─── Estado del grafo ───────────────────────────────
    private Grafos grafo;
    private char letraSiguienteNodo = 'A';

    // ─── Modos de operación ─────────────────────────────
    public enum Modo { EDITAR, JUGAR, BORRAR, PASO_A_PASO }
    private Modo modoActual = Modo.EDITAR;

    // ─── Interacción ────────────────────────────────────
    private Nodo nodoSeleccionado = null;
    private Nodo nodoDragging    = null;
    private int dragOffX, dragOffY;

    // ─── Juego Humano vs IA ─────────────────────────────
    private Nodo nodoJugador  = null;
    private Nodo nodoInicial  = null;
    private double costoPersona = 0.0;
    private Nodo nodoDestino  = null;   // destino elegido por el jugador

    // ─── IA ─────────────────────────────────────────────
    private Nodo nodoIA = null;
    private double costoIA = 0.0;
    private List<Nodo> rutaIA = null;
    private int pasoAnimacionIA = 0;

    // ─── Ruta mostrada (para highlight) ─────────────────
    private List<Nodo> rutaMostrada = null;

    // ─── Paso a paso ────────────────────────────────────
    private List<Grafos.PasoDijkstra> pasosDijkstra = null;
    private int indicePaso = 0;
    private String mensajePaso = "";

    // ─── Imagen de fondo ────────────────────────────────
    private BufferedImage imagenFondo = null;

    // ─── Mensaje en pantalla ────────────────────────────
    private String mensajePantalla = "🖱 Modo EDITAR: Clic para crear nodos. Clic en dos nodos para conectarlos.";

    // ─── Colores ─────────────────────────────────────────
    private static final Color COL_FONDO       = new Color(18, 22, 36);
    private static final Color COL_ARISTA      = new Color(100, 120, 200);
    private static final Color COL_ARISTA_RUTA = new Color(255, 200, 0);
    private static final Color COL_NODO        = new Color(50, 80, 180);
    private static final Color COL_NODO_SELEC  = new Color(200, 80, 200);
    private static final Color COL_NODO_JUGADOR= new Color(50, 220, 100);
    private static final Color COL_NODO_DESTINO= new Color(255, 80, 80);
    private static final Color COL_NODO_IA     = new Color(255, 120, 0);
    private static final Color COL_TEXTO       = new Color(220, 225, 255);
    private static final Color COL_BORDE       = new Color(80, 100, 220);
    private static final Color COL_PASO_BUENO  = new Color(0, 200, 100);
    private static final Color COL_PASO_MALO   = new Color(220, 60, 60);
    private static final int   RADIO           = 22;

    // ─── Referencia al panel lateral ────────────────────
    private JTextArea areaLog;

    public PanelJuego(Grafos grafo, JTextArea areaLog) {
        this.grafo   = grafo;
        this.areaLog = areaLog;
        setBackground(COL_FONDO);
        setFont(new Font("Segoe UI", Font.PLAIN, 13));

        // ── Mouse Listener ──
        MouseAdapter ma = new MouseAdapter() {
            @Override public void mousePressed(MouseEvent e) { onMousePressed(e); }
            @Override public void mouseReleased(MouseEvent e) { onMouseReleased(e); }
            @Override public void mouseDragged(MouseEvent e) { onMouseDragged(e); }
            @Override public void mouseClicked(MouseEvent e) { onMouseClicked(e); }
        };
        addMouseListener(ma);
        addMouseMotionListener(ma);
    }

    // ══════════════════════════════════════════════════════
    // MOUSE EVENTS
    // ══════════════════════════════════════════════════════

    private void onMousePressed(MouseEvent e) {
        if (modoActual == Modo.EDITAR) {
            Nodo bajo = obtenerNodoBajoElRaton(e.getX(), e.getY());
            if (bajo != null && e.getButton() == MouseEvent.BUTTON1) {
                nodoDragging = bajo;
                dragOffX = e.getX() - bajo.getX();
                dragOffY = e.getY() - bajo.getY();
            }
        }
    }

    private void onMouseReleased(MouseEvent e) {
        nodoDragging = null;
    }

    private void onMouseDragged(MouseEvent e) {
        if (nodoDragging != null && modoActual == Modo.EDITAR) {
            // Mover el nodo via reflexión (necesitamos setX/setY o hacemos un workaround)
            try {
                var fx = nodoDragging.getClass().getDeclaredField("x");
                var fy = nodoDragging.getClass().getDeclaredField("y");
                fx.setAccessible(true); fy.setAccessible(true);
                fx.set(nodoDragging, e.getX() - dragOffX);
                fy.set(nodoDragging, e.getY() - dragOffY);
            } catch (Exception ignored) {}
            repaint();
        }
    }

    private void onMouseClicked(MouseEvent e) {
        int mx = e.getX(), my = e.getY();
        Nodo bajo = obtenerNodoBajoElRaton(mx, my);

        switch (modoActual) {
            case EDITAR -> handleEditClick(e, mx, my, bajo);
            case JUGAR  -> handleJugarClick(bajo);
            case BORRAR -> handleBorrarClick(bajo);
        }
        repaint();
    }

    private void handleEditClick(MouseEvent e, int mx, int my, Nodo bajo) {
        if (e.getButton() == MouseEvent.BUTTON3) {
            // Clic derecho: editar peso si es una arista cercana, o borrar nodo
            if (bajo != null) {
                pedirRenombrar(bajo);
            }
            return;
        }

        if (bajo != null) {
            if (nodoSeleccionado == null) {
                nodoSeleccionado = bajo;
                mensajePantalla = "Nodo '" + bajo.getId() + "' seleccionado. Clic en otro para conectar.";
            } else {
                if (nodoSeleccionado != bajo && !nodoSeleccionado.tieneConexionCon(bajo)) {
                    // Pedir peso personalizado
                    String input = JOptionPane.showInputDialog(this,
                        "Peso de la conexión " + nodoSeleccionado.getId() + " ↔ " + bajo.getId() + ":",
                        "Definir peso",
                        JOptionPane.QUESTION_MESSAGE);
                    double peso = 1.0;
                    if (input != null && !input.isBlank()) {
                        try { peso = Double.parseDouble(input.trim()); } catch (NumberFormatException ignored) {}
                    }
                    nodoSeleccionado.agregarArista(bajo, peso);
                    bajo.agregarArista(nodoSeleccionado, peso);
                    log("🔗 Conectado " + nodoSeleccionado.getId() + " ↔ " + bajo.getId() + " (peso=" + peso + ")");
                } else if (nodoSeleccionado != bajo) {
                    mensajePantalla = "⚠ Ya existe esa conexión.";
                }
                nodoSeleccionado = null;
            }
        } else {
            // Crear nodo nuevo
            String id = String.valueOf(letraSiguienteNodo++);
            grafo.agregarNodo(new Nodo(id, mx, my));
            mensajePantalla = "✅ Nodo '" + id + "' creado.";
            log("➕ Nodo '" + id + "' creado en (" + mx + "," + my + ")");
            nodoSeleccionado = null;
        }
    }

    private void handleJugarClick(Nodo bajo) {
        if (bajo == null) return;

        if (nodoJugador == null) {
            nodoJugador = bajo;
            nodoInicial = bajo;
            costoPersona = 0.0;
            mensajePantalla = "🚀 Inicio en '" + bajo.getId() + "'. Elige tu destino haciendo clic.";
            log("🟢 Jugador inició en: " + bajo.getId());
        } else if (nodoDestino == null && bajo != nodoJugador) {
            nodoDestino = bajo;
            mensajePantalla = "🎯 Destino: '" + bajo.getId() + "'. Ahora navega hasta él.";
            log("🎯 Destino elegido: " + bajo.getId());
        } else if (nodoJugador.tieneConexionCon(bajo)) {
            double costo = nodoJugador.obtenerCostoHacia(bajo);
            costoPersona += costo;
            log("👣 Movido a '" + bajo.getId() + "' (+costo " + costo + " = total " + costoPersona + ")");
            nodoJugador = bajo;
            mensajePantalla = "Moviste a '" + bajo.getId() + "' | Costo acum.: " + costoPersona;

            if (nodoDestino != null && nodoJugador == nodoDestino) {
                mensajePantalla = "🏁 ¡Llegaste a '" + nodoDestino.getId() + "'! Costo total: " + costoPersona + ". Presiona 'Lanzar IA' para comparar.";
                log("🏁 Jugador llegó al destino con costo: " + costoPersona);
            }
        } else {
            mensajePantalla = "⛔ No hay conexión directa desde '" + nodoJugador.getId() + "' a '" + bajo.getId() + "'.";
        }
    }

    private void handleBorrarClick(Nodo bajo) {
        if (bajo != null) {
            int confirm = JOptionPane.showConfirmDialog(this,
                "¿Eliminar nodo '" + bajo.getId() + "'?", "Confirmar", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                grafo.eliminarNodo(bajo.getId());
                log("🗑 Nodo '" + bajo.getId() + "' eliminado.");
                mensajePantalla = "Nodo eliminado.";
            }
        } else {
            mensajePantalla = "Clic sobre un nodo para eliminarlo.";
        }
    }

    // ══════════════════════════════════════════════════════
    // PAINT
    // ══════════════════════════════════════════════════════

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // Fondo: imagen o color sólido
        if (imagenFondo != null) {
            g2.drawImage(imagenFondo, 0, 0, getWidth(), getHeight(), null);
            // Capa semi-transparente oscura encima para legibilidad
            g2.setColor(new Color(0, 0, 0, 120));
            g2.fillRect(0, 0, getWidth(), getHeight());
        }

        if (grafo == null) return;

        // Aristas
        dibujarAristas(g2);

        // Nodos
        dibujarNodos(g2);

        // HUD (mensajes, costos, modo)
        dibujarHUD(g2);
    }

    private void dibujarAristas(Graphics2D g2) {
        g2.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        for (Nodo origen : grafo.getTodosLosNodos()) {
            for (Arista arista : origen.getAdyacentes()) {
                Nodo destino = arista.getDestino();
                // Solo dibujar en un sentido para no duplicar
                if (origen.getId().compareTo(destino.getId()) > 0) continue;

                boolean enRuta = rutaMostrada != null && enRutaArista(origen, destino);

                if (enRuta) {
                    // Glow efecto para la ruta
                    g2.setColor(new Color(255, 200, 0, 60));
                    g2.setStroke(new BasicStroke(10f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    g2.drawLine(origen.getX(), origen.getY(), destino.getX(), destino.getY());
                    g2.setColor(COL_ARISTA_RUTA);
                    g2.setStroke(new BasicStroke(3.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                } else {
                    g2.setColor(COL_ARISTA);
                    g2.setStroke(new BasicStroke(1.8f));
                }
                g2.drawLine(origen.getX(), origen.getY(), destino.getX(), destino.getY());

                // Peso
                int medX = (origen.getX() + destino.getX()) / 2;
                int medY = (origen.getY() + destino.getY()) / 2;
                String peso = String.valueOf((int) arista.getPeso());

                // Fondo del peso
                g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
                FontMetrics fm = g2.getFontMetrics();
                int tw = fm.stringWidth(peso);
                g2.setColor(new Color(18, 22, 36, 200));
                g2.fillRoundRect(medX - tw/2 - 4, medY - 13, tw + 8, 17, 6, 6);

                g2.setColor(enRuta ? COL_ARISTA_RUTA : new Color(200, 210, 255));
                g2.drawString(peso, medX - tw/2, medY);
            }
        }
    }

    private void dibujarNodos(Graphics2D g2) {
        for (Nodo nodo : grafo.getTodosLosNodos()) {
            int x = nodo.getX(), y = nodo.getY();
            boolean enRuta = rutaMostrada != null && rutaMostrada.contains(nodo);

            // Color base del nodo
            Color colorNodo;
            if      (nodo == nodoSeleccionado)            colorNodo = COL_NODO_SELEC;
            else if (nodo == nodoJugador && modoActual == Modo.JUGAR) colorNodo = COL_NODO_JUGADOR;
            else if (nodo == nodoDestino)                 colorNodo = COL_NODO_DESTINO;
            else if (nodo == nodoIA)                      colorNodo = COL_NODO_IA;
            else if (enRuta)                              colorNodo = new Color(255, 200, 50);
            else                                          colorNodo = COL_NODO;

            // Sombra
            g2.setColor(new Color(0, 0, 0, 80));
            g2.fillOval(x - RADIO + 3, y - RADIO + 3, RADIO * 2, RADIO * 2);

            // Glow si está en ruta
            if (enRuta) {
                g2.setColor(new Color(255, 200, 0, 50));
                g2.fillOval(x - RADIO - 5, y - RADIO - 5, RADIO * 2 + 10, RADIO * 2 + 10);
            }

            // Relleno con gradiente
            GradientPaint gp = new GradientPaint(
                x - RADIO, y - RADIO, colorNodo.brighter(),
                x + RADIO, y + RADIO, colorNodo.darker()
            );
            g2.setPaint(gp);
            g2.fillOval(x - RADIO, y - RADIO, RADIO * 2, RADIO * 2);

            // Borde
            g2.setStroke(new BasicStroke(2f));
            g2.setColor(nodo == nodoSeleccionado ? Color.WHITE : COL_BORDE.brighter());
            g2.drawOval(x - RADIO, y - RADIO, RADIO * 2, RADIO * 2);

            // Etiqueta ID
            g2.setFont(new Font("Segoe UI", Font.BOLD, 13));
            FontMetrics fm = g2.getFontMetrics();
            String label = nodo.getId().length() > 3 ? nodo.getId().substring(0, 3) : nodo.getId();
            g2.setColor(Color.WHITE);
            g2.drawString(label, x - fm.stringWidth(label) / 2, y + 5);

            // Mostrar distancia si Dijkstra corrió
            if (nodo.getDistanciaMinima() != Double.POSITIVE_INFINITY && rutaMostrada != null) {
                String dist = "d=" + (int) nodo.getDistanciaMinima();
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 10));
                g2.setColor(new Color(200, 255, 200));
                g2.drawString(dist, x - fm.stringWidth(dist)/2, y + RADIO + 14);
            }
        }
    }

    private void dibujarHUD(Graphics2D g2) {
        // Barra superior con mensaje
        g2.setColor(new Color(10, 14, 30, 200));
        g2.fillRoundRect(10, 8, getWidth() - 20, 30, 10, 10);

        g2.setFont(new Font("Segoe UI", Font.BOLD, 13));
        g2.setColor(COL_TEXTO);
        g2.drawString(mensajePantalla, 20, 28);

        // Indicador de modo
        String modoStr = switch (modoActual) {
            case EDITAR -> "✏ EDITAR";
            case JUGAR  -> "🎮 JUGAR";
            case BORRAR -> "🗑 BORRAR";
            case PASO_A_PASO -> "🔍 PASO A PASO";
        };
        Color modoColor = switch (modoActual) {
            case EDITAR -> new Color(80, 150, 255);
            case JUGAR  -> new Color(80, 220, 100);
            case BORRAR -> new Color(220, 80, 80);
            case PASO_A_PASO -> new Color(220, 180, 50);
        };
        g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
        FontMetrics fm = g2.getFontMetrics();
        int mw = fm.stringWidth(modoStr);
        g2.setColor(new Color(modoColor.getRed(), modoColor.getGreen(), modoColor.getBlue(), 50));
        g2.fillRoundRect(getWidth() - mw - 24, 8, mw + 14, 26, 8, 8);
        g2.setColor(modoColor);
        g2.drawString(modoStr, getWidth() - mw - 17, 26);

        // Costos en modo jugar
        if (modoActual == Modo.JUGAR || rutaMostrada != null) {
            int baseY = 48;
            g2.setColor(new Color(10, 14, 30, 180));
            g2.fillRoundRect(10, baseY, 250, 55, 10, 10);

            g2.setFont(new Font("Segoe UI", Font.BOLD, 13));
            g2.setColor(COL_NODO_JUGADOR);
            g2.drawString("🧍 Tu costo: " + costoPersona, 20, baseY + 18);
            g2.setColor(COL_NODO_IA);
            g2.drawString("🤖 IA (Dijkstra): " + costoIA, 20, baseY + 38);
        }

        // Leyenda de nodos especiales
        if (modoActual == Modo.JUGAR) {
            dibujarLeyenda(g2);
        }

        // Paso a paso
        if (modoActual == Modo.PASO_A_PASO && !mensajePaso.isEmpty()) {
            g2.setColor(new Color(10, 14, 30, 210));
            g2.fillRoundRect(10, getHeight() - 55, getWidth() - 20, 45, 10, 10);
            g2.setFont(new Font("Segoe UI", Font.BOLD, 13));

            boolean bueno = mensajePaso.startsWith("✅");
            g2.setColor(bueno ? COL_PASO_BUENO : COL_PASO_MALO);
            g2.drawString(mensajePaso, 20, getHeight() - 28);

            if (pasosDijkstra != null) {
                g2.setColor(new Color(150, 160, 200));
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
                g2.drawString("Paso " + indicePaso + " de " + pasosDijkstra.size(), 20, getHeight() - 12);
            }
        }
    }

    private void dibujarLeyenda(Graphics2D g2) {
        int bx = getWidth() - 170, by = 50;
        g2.setColor(new Color(10, 14, 30, 180));
        g2.fillRoundRect(bx - 10, by - 5, 165, 80, 10, 10);

        g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        dibujarItemLeyenda(g2, bx, by + 12,  COL_NODO_JUGADOR, "Tú");
        dibujarItemLeyenda(g2, bx, by + 30,  COL_NODO_DESTINO, "Destino");
        dibujarItemLeyenda(g2, bx, by + 48,  COL_NODO_IA,      "IA (Dijkstra)");
        dibujarItemLeyenda(g2, bx, by + 66,  new Color(255, 200, 50), "Ruta óptima");
    }

    private void dibujarItemLeyenda(Graphics2D g2, int x, int y, Color c, String label) {
        g2.setColor(c);
        g2.fillOval(x, y - 9, 12, 12);
        g2.setColor(COL_TEXTO);
        g2.drawString(label, x + 18, y);
    }

    // ══════════════════════════════════════════════════════
    // ACCIONES PÚBLICAS (llamadas desde VentanaPrincipal)
    // ══════════════════════════════════════════════════════

    public void setModo(Modo m) {
        this.modoActual = m;
        nodoSeleccionado = null;
        mensajePantalla = switch (m) {
            case EDITAR -> "🖱 Modo EDITAR: Clic vacío=nuevo nodo, clic nodo+nodo=conectar.";
            case JUGAR  -> "🎮 Modo JUGAR: Clic 1er nodo=inicio, clic 2do nodo=destino, navega.";
            case BORRAR -> "🗑 Modo BORRAR: Clic sobre un nodo para eliminarlo.";
            case PASO_A_PASO -> "🔍 Elige nodo ORIGEN y presiona 'Iniciar Paso a Paso'.";
        };
        repaint();
    }

    public Modo getModoActual() { return modoActual; }

    public void lanzarIA() {
        if (nodoInicial == null || nodoJugador == null) {
            mensajePantalla = "⚠ Primero debes jugar y elegir un destino.";
            repaint(); return;
        }
        String destId = nodoDestino != null ? nodoDestino.getId() : nodoJugador.getId();

        for (Nodo n : grafo.getTodosLosNodos()) n.resetear();
        grafo.ejecutarDijkstra(nodoInicial.getId());
        rutaIA = grafo.obtenerRutaMasCorta(destId);
        rutaMostrada = rutaIA;

        if (rutaIA.isEmpty()) {
            mensajePantalla = "Dijkstra: no hay camino posible.";
            repaint(); return;
        }

        pasoAnimacionIA = 0; costoIA = 0.0;
        nodoIA = rutaIA.get(0);
        mensajePantalla = "🤖 IA calculando...";

        // Log tabla de distancias
        logDistancias();

        Timer t = new Timer(900, e -> {
            pasoAnimacionIA++;
            if (pasoAnimacionIA < rutaIA.size()) {
                Nodo sig = rutaIA.get(pasoAnimacionIA);
                costoIA += nodoIA.obtenerCostoHacia(sig);
                nodoIA = sig;
                repaint();
            } else {
                ((Timer) e.getSource()).stop();
                nodoIA = rutaIA.get(rutaIA.size() - 1);
                if      (costoIA < costoPersona) mensajePantalla = "🤖 ¡Gana la IA! Costo: " + costoIA + " vs tu " + costoPersona;
                else if (costoIA == costoPersona) mensajePantalla = "🤝 ¡Empate! Encontraste la ruta óptima.";
                else mensajePantalla = "🏆 ¡Ganaste tú! Increíble.";
                log("─── Resultado: IA=" + costoIA + " | Humano=" + costoPersona);
                repaint();
            }
        });
        t.start();
    }

    public void reiniciar() {
        for (Nodo n : grafo.getTodosLosNodos()) n.resetear();
        nodoJugador = null; nodoInicial = null; nodoDestino = null;
        costoPersona = 0; nodoIA = null; costoIA = 0;
        if (rutaIA != null) rutaIA.clear();
        rutaMostrada = null;
        mensajePantalla = "♻ Partida reiniciada.";
        repaint();
    }

    public void borrarTodo() {
        grafo.limpiarTodo();
        letraSiguienteNodo = 'A';
        reiniciar();
        mensajePantalla = "🗑 Grafo borrado. Crea un nuevo mapa.";
        repaint();
    }

    /** Modo paso a paso: iniciar desde nodo origen seleccionado */
    public void iniciarPasoAPaso(String origenId) {
        for (Nodo n : grafo.getTodosLosNodos()) n.resetear();
        pasosDijkstra = grafo.ejecutarDijkstraPasoAPaso(origenId);

        // Reseteamos para mostrar desde cero
        for (Nodo n : grafo.getTodosLosNodos()) n.resetear();
        grafo.getNodo(origenId).setDistanciaMinima(0);

        indicePaso = 0;
        rutaMostrada = null;
        mensajePaso = "Paso 0: Origen '" + origenId + "' dist=0. Presiona '▶ Siguiente'.";
        log("🔍 Paso a paso desde: " + origenId + " (" + pasosDijkstra.size() + " pasos)");
        repaint();
    }

    public void siguientePaso() {
        if (pasosDijkstra == null || indicePaso >= pasosDijkstra.size()) {
            mensajePaso = "✅ Dijkstra completado.";
            repaint(); return;
        }
        Grafos.PasoDijkstra paso = pasosDijkstra.get(indicePaso);
        mensajePaso = paso.descripcion;
        log("Paso " + (indicePaso + 1) + ": " + paso.descripcion);

        // Aplicar la relajación si mejoró
        if (paso.mejorado) {
            Nodo vecino  = grafo.getNodo(paso.vecino);
            Nodo actual  = grafo.getNodo(paso.nodoActual);
            if (vecino != null) {
                vecino.setDistanciaMinima(paso.nuevaDistancia);
                vecino.setPredecesor(actual);
            }
        }
        indicePaso++;
        repaint();
    }

    /** Poner imagen de fondo */
    public void setImagenFondo(BufferedImage img) {
        this.imagenFondo = img;
        repaint();
    }

    public void quitarImagenFondo() {
        this.imagenFondo = null;
        repaint();
    }

    /** Calcular y mostrar ruta entre dos nodos elegidos sin modo juego */
    public void calcularRutaDirecta(String origenId, String destinoId) {
        for (Nodo n : grafo.getTodosLosNodos()) n.resetear();
        grafo.ejecutarDijkstra(origenId);
        List<Nodo> ruta = grafo.obtenerRutaMasCorta(destinoId);

        if (ruta.isEmpty()) {
            mensajePantalla = "⚠ No hay ruta de '" + origenId + "' a '" + destinoId + "'.";
        } else {
            rutaMostrada = ruta;
            double costo = grafo.getNodo(destinoId).getDistanciaMinima();
            StringBuilder sb = new StringBuilder("🔎 Ruta: ");
            for (int i = 0; i < ruta.size(); i++) {
                if (i > 0) sb.append(" → ");
                sb.append(ruta.get(i).getId());
            }
            sb.append(" | Costo: ").append(costo);
            mensajePantalla = sb.toString();
            log(sb.toString());
            logDistancias();
        }
        repaint();
    }

    // ══════════════════════════════════════════════════════
    // HELPERS
    // ══════════════════════════════════════════════════════

    private Nodo obtenerNodoBajoElRaton(int mx, int my) {
        for (Nodo n : grafo.getTodosLosNodos()) {
            double d = Math.hypot(mx - n.getX(), my - n.getY());
            if (d <= RADIO) return n;
        }
        return null;
    }

    private boolean enRutaArista(Nodo a, Nodo b) {
        for (int i = 0; i < rutaMostrada.size() - 1; i++) {
            Nodo ra = rutaMostrada.get(i), rb = rutaMostrada.get(i + 1);
            if ((ra == a && rb == b) || (ra == b && rb == a)) return true;
        }
        return false;
    }

    private void pedirRenombrar(Nodo nodo) {
        String nuevo = JOptionPane.showInputDialog(this, "Nuevo ID para el nodo:", nodo.getId());
        if (nuevo != null && !nuevo.isBlank()) {
            try {
                var f = nodo.getClass().getDeclaredField("id");
                f.setAccessible(true);
                f.set(nodo, nuevo.trim());
                mensajePantalla = "Nodo renombrado a '" + nuevo.trim() + "'.";
            } catch (Exception ignored) {}
        }
    }

    private void logDistancias() {
        log("── Distancias mínimas ──");
        for (Nodo n : grafo.getTodosLosNodos()) {
            String d = n.getDistanciaMinima() == Double.POSITIVE_INFINITY ? "∞" : String.valueOf((int)n.getDistanciaMinima());
            log("  " + n.getId() + ": " + d);
        }
    }

    private void log(String msg) {
        if (areaLog != null) {
            areaLog.append(msg + "\n");
            areaLog.setCaretPosition(areaLog.getDocument().getLength());
        }
    }

    public Grafos getGrafo() { return grafo; }


}