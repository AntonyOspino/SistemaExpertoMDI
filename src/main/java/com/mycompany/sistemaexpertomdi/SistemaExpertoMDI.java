package com.mycompany.sistemaexpertomdi;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyVetoException;

/**
 *
 * @author ALEJO
 */
public class SistemaExpertoMDI extends JFrame {
    
    private JDesktopPane desktopPane;
    
    public SistemaExpertoMDI(){
         super("Sistema Experto Cardiovascular - Diagnóstico");
         this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
         this.setExtendedState(JFrame.MAXIMIZED_BOTH); // Pantalla completa
         this.setSize(1200, 800);
         
         desktopPane = new JDesktopPane();
         this.setContentPane(desktopPane);
         
         // Abrir directamente el formulario unificado al iniciar
         openConsultaUnificada();
    }
    
    private void openConsultaUnificada(){
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