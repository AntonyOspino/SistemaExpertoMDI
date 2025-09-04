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
        //desktopPane.setBackground(new Color(171, 214, 237));
        this.setContentPane(desktopPane);

        // Abrir directamente el formulario unificado al iniciar
        openConsultaUnificada();
    }

    private void openConsultaUnificada() {
        ConsultaUnificadaFrame consulta = new ConsultaUnificadaFrame("Sistema Cardiovascular", 1000, 700);

        desktopPane.add(consulta);
        consulta.setVisible(true);

        try {
            // Centrar la ventana interna
            consulta.setLocation(
                    (desktopPane.getWidth() - consulta.getWidth()) / 2,
                    (desktopPane.getHeight() - consulta.getHeight()) / 2
            );

            // Maximizar para que ocupe todo el espacio disponible
            consulta.setMaximum(true);
            consulta.setSelected(true);
        } catch (PropertyVetoException e) {
            e.printStackTrace();
        }
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

        // --- FIN DE CAMBIOS GLOBALES ---
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                SistemaExpertoMDI mainFrame = new SistemaExpertoMDI();
                mainFrame.setVisible(true);
                mainFrame.setLocationRelativeTo(null);
            }
        });
    }
}
