package fronted;

import core.Grafos;
import fronted.PanelJuego;

import java.awt.*;
import javax.swing.*;

public class VentanaPrincipal extends JFrame {

    public VentanaPrincipal() {

        this.setTitle("DIJKSTRA");
        this.setSize(1100, 650);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setLayout(new BorderLayout());

        // ── Grafo inicial ──────────────────────────────────────────
        Grafos miGrafo = new Grafos();

        // ── Panel de log (lateral derecho) ─────────────────────────
        JTextArea areaLog = new JTextArea();
        areaLog.setEditable(false);
        areaLog.setBackground(new Color(12, 16, 28));
        areaLog.setForeground(new Color(180, 200, 255));
        areaLog.setFont(new Font("Consolas", Font.PLAIN, 12));
        areaLog.setMargin(new Insets(6, 6, 6, 6));

        JScrollPane scrollLog = new JScrollPane(areaLog);
        scrollLog.setPreferredSize(new Dimension(260, 0));
        scrollLog.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(60, 80, 160)),
                "📋 Log", 0, 0,
                new Font("Segoe UI", Font.BOLD, 12), new Color(180, 200, 255)));
        scrollLog.getViewport().setBackground(new Color(12, 16, 28));

        // ── Lienzo principal ───────────────────────────────────────
        PanelJuego lienzo = new PanelJuego(miGrafo, areaLog);
        this.add(lienzo, BorderLayout.CENTER);
        this.add(scrollLog, BorderLayout.EAST);

        // ══════════════════════════════════════════════════════════
        // PANEL DE BOTONES (parte inferior)
        // ══════════════════════════════════════════════════════════
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        panelBotones.setBackground(new Color(10, 14, 28));

        // ── Botones de modo ────────────────────────────────────────
        JButton btnEditar = crearBoton("✏ Editar", new Color(50, 90, 200));
        JButton btnJugar  = crearBoton("🎮 Jugar",  new Color(40, 160, 80));
        JButton btnBorrar = crearBoton("🗑 Borrar", new Color(180, 50, 50));
        JButton btnPasos  = crearBoton("🔍 Paso a Paso", new Color(160, 130, 30));

        btnEditar.addActionListener(e -> lienzo.setModo(PanelJuego.Modo.EDITAR));
        btnJugar .addActionListener(e -> lienzo.setModo(PanelJuego.Modo.JUGAR));
        btnBorrar.addActionListener(e -> lienzo.setModo(PanelJuego.Modo.BORRAR));
        btnPasos .addActionListener(e -> {
            lienzo.setModo(PanelJuego.Modo.PASO_A_PASO);
            String[] ids = lienzo.getGrafo().getTodosLosNodos()
                    .stream().map(n -> n.getId()).toArray(String[]::new);
            if (ids.length == 0) {
                JOptionPane.showMessageDialog(this, "No hay nodos en el grafo.");
                return;
            }
            String origen = (String) JOptionPane.showInputDialog(
                    this, "Selecciona el nodo ORIGEN:", "Paso a Paso",
                    JOptionPane.QUESTION_MESSAGE, null, ids, ids[0]);
            if (origen != null) lienzo.iniciarPasoAPaso(origen);
        });

        // ── Separador visual ───────────────────────────────────────
        JSeparator sep1 = new JSeparator(SwingConstants.VERTICAL);
        sep1.setPreferredSize(new Dimension(2, 28));
        sep1.setForeground(new Color(60, 70, 120));

        // ── Botones de acción ──────────────────────────────────────
        JButton btnIA = crearBoton("🤖 Lanzar IA", new Color(200, 80, 0));
        btnIA.addActionListener(e -> {
            if (lienzo.getModoActual() == PanelJuego.Modo.JUGAR) {
                lienzo.lanzarIA();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Cambia a modo JUGAR primero y establece origen/destino.");
            }
        });

        JButton btnSiguientePaso = crearBoton("▶ Siguiente paso", new Color(130, 100, 20));
        btnSiguientePaso.addActionListener(e -> {
            if (lienzo.getModoActual() == PanelJuego.Modo.PASO_A_PASO) {
                lienzo.siguientePaso();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Activa el modo PASO A PASO primero.");
            }
        });

        JButton btnRuta = crearBoton("🔎 Calcular ruta", new Color(30, 110, 150));
        btnRuta.addActionListener(e -> {
            String[] ids = lienzo.getGrafo().getTodosLosNodos()
                    .stream().map(n -> n.getId()).toArray(String[]::new);
            if (ids.length < 2) {
                JOptionPane.showMessageDialog(this, "Necesitas al menos 2 nodos.");
                return;
            }
            String origen = (String) JOptionPane.showInputDialog(
                    this, "Nodo ORIGEN:", "Calcular ruta",
                    JOptionPane.QUESTION_MESSAGE, null, ids, ids[0]);
            if (origen == null) return;
            String destino = (String) JOptionPane.showInputDialog(
                    this, "Nodo DESTINO:", "Calcular ruta",
                    JOptionPane.QUESTION_MESSAGE, null, ids, ids[ids.length - 1]);
            if (destino != null) lienzo.calcularRutaDirecta(origen, destino);
        });

        // ── Separador visual ───────────────────────────────────────
        JSeparator sep2 = new JSeparator(SwingConstants.VERTICAL);
        sep2.setPreferredSize(new Dimension(2, 28));
        sep2.setForeground(new Color(60, 70, 120));

        // ── Mapas predefinidos ─────────────────────────────────────
        JButton btnMapaCiudad = crearBoton("🗺 Mapa Ciudad", new Color(40, 100, 60));
        btnMapaCiudad.addActionListener(e -> {
            miGrafo.cargarMapaCiudad(lienzo.getWidth(), lienzo.getHeight());
            lienzo.setModo(PanelJuego.Modo.EDITAR);
            areaLog.append("✅ Mapa Ciudad cargado.\n");
        });

        JButton btnRedServ = crearBoton("🖧 Red Servidor", new Color(40, 60, 120));
        btnRedServ.addActionListener(e -> {
            miGrafo.cargarRedServidor(lienzo.getWidth(), lienzo.getHeight());
            lienzo.setModo(PanelJuego.Modo.EDITAR);
            areaLog.append("✅ Red Servidor cargada.\n");
        });

        // ── Separador visual ───────────────────────────────────────
        JSeparator sep3 = new JSeparator(SwingConstants.VERTICAL);
        sep3.setPreferredSize(new Dimension(2, 28));
        sep3.setForeground(new Color(60, 70, 120));

        // ── Utilidades ─────────────────────────────────────────────
        JButton btnReiniciar = crearBoton("♻ Reiniciar", new Color(60, 60, 60));
        btnReiniciar.addActionListener(e -> lienzo.reiniciar());

        JButton btnBorrarTodo = crearBoton("💣 Borrar todo", new Color(100, 20, 20));
        btnBorrarTodo.addActionListener(e -> {
            int ok = JOptionPane.showConfirmDialog(this,
                    "¿Borrar todo el grafo?", "Confirmar", JOptionPane.YES_NO_OPTION);
            if (ok == JOptionPane.YES_OPTION) lienzo.borrarTodo();
        });

        JButton btnExportar = crearBoton("📤 Exportar", new Color(30, 80, 80));
        btnExportar.addActionListener(e -> {
            String texto = miGrafo.exportarComoTexto();
            JTextArea ta = new JTextArea(texto);
            ta.setEditable(false);
            JOptionPane.showMessageDialog(this,
                    new JScrollPane(ta), "Grafo exportado", JOptionPane.INFORMATION_MESSAGE);
        });

        // ── Añadir todos al panel ──────────────────────────────────
        panelBotones.add(btnEditar);
        panelBotones.add(btnJugar);
        panelBotones.add(btnBorrar);
        panelBotones.add(btnPasos);
        panelBotones.add(sep1);
        panelBotones.add(btnIA);
        panelBotones.add(btnSiguientePaso);
        panelBotones.add(btnRuta);
        panelBotones.add(sep2);
        panelBotones.add(btnMapaCiudad);
        panelBotones.add(btnRedServ);
        panelBotones.add(sep3);
        panelBotones.add(btnReiniciar);
        panelBotones.add(btnBorrarTodo);
        panelBotones.add(btnExportar);

        this.add(panelBotones, BorderLayout.SOUTH);
    }

    // ── Helper para crear botones con estilo uniforme ──────────────
    private JButton crearBoton(String texto, Color fondo) {
        JButton btn = new JButton(texto);
        btn.setBackground(fondo);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(fondo.brighter(), 1),
                BorderFactory.createEmptyBorder(4, 10, 4, 10)));
        return btn;
    }
}