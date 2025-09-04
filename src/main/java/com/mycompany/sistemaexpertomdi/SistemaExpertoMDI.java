package com.mycompany.sistemaexpertomdi;

import com.formdev.flatlaf.FlatLightLaf;
import javax.swing.*;
import java.awt.*;
import java.beans.PropertyVetoException;

/**
 *
 * @author ALEJO
 */
public class SistemaExpertoMDI extends JFrame {

    private JDesktopPane desktopPane;

    public SistemaExpertoMDI() {
        super("Sistema Experto Cardiovascular - Diagnóstico");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setExtendedState(JFrame.MAXIMIZED_BOTH); // Pantalla completa
        this.setSize(1200, 800);

        desktopPane = new JDesktopPane();
        desktopPane.setBackground(new Color(240, 240, 240)); // Fondo para evitar que desaparezca la interfaz

        // Creamos un panel principal para el JFrame para poder añadir el toolbar y el desktopPane
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.add(desktopPane, BorderLayout.CENTER);

        contentPanel.add(createMenuBar(), BorderLayout.NORTH); // El menú ahora va en un panel, arriba
        contentPanel.add(desktopPane, BorderLayout.CENTER); // El desktop pane ocupa el resto
        this.setContentPane(contentPanel);
        //this.setJMenuBar(createMenuBar());
        // Abrir el formulario unificado al iniciar
        openConsultaUnificada();
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        // Menú Archivo
        JMenu menuArchivo = new JMenu("Archivo");

        // Opción del menú para abrir la consulta
        JMenuItem itemAbrirConsulta = new JMenuItem("Abrir Consulta");
        itemAbrirConsulta.addActionListener(e -> openConsultaUnificada());

        menuArchivo.add(itemAbrirConsulta);
        menuBar.add(menuArchivo);

        return menuBar;
    }

    private void openConsultaUnificada() {
    // 1. Valida si la ventana ya está abierta
    JInternalFrame[] frames = desktopPane.getAllFrames();
    for (JInternalFrame frame : frames) {
        if (frame instanceof ConsultaUnificadaFrame) {
            try {
                frame.setSelected(true);
                frame.moveToFront(); // Lo mueve al frente
                return; // Si ya está, salimos del método
            } catch (PropertyVetoException e) {
                e.printStackTrace();
            }
        }
    }
    
    // 2. Si no está abierta, crea una nueva instancia
    ConsultaUnificadaFrame consulta = new ConsultaUnificadaFrame("Sistema Cardiovascular", 700, 600);

    // 3. Añade la ventana al desktopPane
    desktopPane.add(consulta);
    
    // --- LÍNEA CLAVE: TODO lo demás debe ir aquí dentro ---
    SwingUtilities.invokeLater(() -> {
        try {
            // Centra la ventana interna (esto ahora sí funcionará)
            int x = (desktopPane.getWidth() - consulta.getWidth()) / 2;
            int y = (desktopPane.getHeight() - consulta.getHeight()) / 2;
            consulta.setLocation(x, y);

            // Haz la ventana visible y selecciónala
            consulta.setVisible(true);
            consulta.setSelected(true);

        } catch (PropertyVetoException e) {
            e.printStackTrace();
        }
    });
}

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (UnsupportedLookAndFeelException ex) {
            System.err.println("Failed to initialize FlatLaf");
        }

        // --- CAMBIOS GLOBALES PARA LA TABLA ---
        UIManager.put("TableHeader.background", new Color(60, 141, 188));
        UIManager.put("TableHeader.foreground", Color.WHITE);
        UIManager.put("TableHeader.font", new Font("Segoe UI", Font.BOLD, 14));
        UIManager.put("Table.selectionBackground", new Color(171, 214, 237));
        UIManager.put("Table.selectionForeground", Color.BLACK);
        UIManager.put("Table.rowHeight", 25);

        Color menuBarColor = new Color(240, 240, 240);
        UIManager.put("MenuBar.background", menuBarColor);
        UIManager.put("Menu.background", menuBarColor);
        UIManager.put("MenuItem.background", menuBarColor);

        // Opcional: Para cambiar el color al pasar el mouse
        UIManager.put("Menu.selectionBackground", new Color(171, 214, 237));
        UIManager.put("MenuItem.selectionBackground", new Color(171, 214, 237));

        // --- FIN DE CAMBIOS GLOBALES ---
        SwingUtilities.invokeLater(() -> {
            SistemaExpertoMDI mainFrame = new SistemaExpertoMDI();
            mainFrame.setVisible(true);
            mainFrame.setLocationRelativeTo(null);
        });
    }
}
