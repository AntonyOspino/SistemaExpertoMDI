/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.sistemaexpertomdi;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyVetoException;

/**
 *
 * @author ALEJO
 */
public class SistemaExpertoMDI extends JFrame {
    
    private JDesktopPane desktopPane;
    private int frameCounter = 0;
    
    public SistemaExpertoMDI(){
         super("Diagnostico sistema Cardiovascular");
         this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
         this.setSize(800, 600);
         
         desktopPane = new JDesktopPane();
         this.setContentPane(desktopPane);
         
         createMenu();
    }
    
    private void createMenu(){
    //Barra de Menu
    JMenuBar menu = new JMenuBar();
    //Items desplegables y botones del menu
    JMenu navegarMenu = new JMenu("Navegar");
    JMenu archivoMenu = new JMenu("Archivo");
    JMenuItem consultaItem = new JMenuItem("Consulta");
    JMenuItem historialItem = new JMenuItem("Historial");
    JMenuItem salirMenu = new JMenuItem("Salir");
    
    //Action Listener boton salir
    salirMenu.addActionListener((ActionEvent e)-> System.exit(0));
    //Añadiendo los demas botones
    consultaItem.addActionListener((ActionEvent e) -> {
        openConsultaForm();
    });
    
    
    historialItem.addActionListener((ActionEvent e) -> {
        openHistorialForm();
    });
    
    MouseAdapter openMenuMouseAdapter = new MouseAdapter() {
        @Override
        public void mouseEntered(MouseEvent e) {
            JMenu sourceMenu = (JMenu) e.getSource();
            sourceMenu.doClick();
        }
    };
    
    archivoMenu.addMouseListener(openMenuMouseAdapter);
    navegarMenu.addMouseListener(openMenuMouseAdapter);
    
    menu.addMouseListener(new MouseAdapter() {
        @Override
        public void mouseExited(MouseEvent e) {
            // Obtiene la posición del cursor
            Point p = e.getPoint();
            // Si el cursor está fuera de la barra de menú, cierra los menús
            if (!menu.contains(p)) {
                MenuSelectionManager.defaultManager().clearSelectedPath();
            }
        }
    });
    
    
    archivoMenu.add(salirMenu);
    navegarMenu.add(consultaItem);
    navegarMenu.add(historialItem);
    menu.add(archivoMenu);
    menu.add(navegarMenu);
    this.setJMenuBar(menu);
    }
    
    private void openConsultaForm(){
    frameCounter++;
    ConsultaInternalFrame consulta = new ConsultaInternalFrame("Consultar diagnostico", 800, 500);
    
    desktopPane.add(consulta);
    consulta.setVisible(true);
    
    try {
        // Maximiza la ventana interna para que ocupe todo el espacio
        consulta.setMaximum(true);
        // También puedes seleccionar la ventana para que se enfoque
        consulta.setSelected(true); 
    } catch (PropertyVetoException e) {
        e.printStackTrace();
    }
}
    
    private void openHistorialForm(){
    frameCounter++;
    HistorialInternalFrame historial = new HistorialInternalFrame("Consultar diagnostico", 400, 300);
    
    desktopPane.add(historial);
    historial.setVisible(true);
    
    try {
        // Maximiza la ventana interna para que ocupe todo el espacio
        historial.setMaximum(true);
        // También puedes seleccionar la ventana para que se enfoque
        historial.setSelected(true); 
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
