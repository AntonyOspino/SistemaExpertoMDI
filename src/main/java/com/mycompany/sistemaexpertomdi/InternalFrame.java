package com.mycompany.sistemaexpertomdi;


import javax.swing.JInternalFrame;

public class InternalFrame extends JInternalFrame {
    public InternalFrame(String title){
        super(title,
                true,//resizable
                true,//closable
                true,//maximizable
                true);//inconifiable
        
        // Aqui se pueden añadir los componentes del formilario
        this.setSize(800, 500);        
    }
    
    public InternalFrame(String title, int width, int height){
    super(title, true, true, true, true);
    this.setSize(width, height);
    }
}
