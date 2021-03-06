/*
 * AutomataApplet.java
 *
 * Created on 10. helmikuuta 2004, 19:40
 */

package org.rikastamo.automata;
//import java.util.Vector;
/**
 *
 * @author  rannala
 */
public class AutomataApplet extends javax.swing.JApplet {
     int[] kultainen_x, kultainen_y;
     int width = 600, height = 600;
    /** Initializes the applet AutomataApplet */
    public void init() {
        initComponents();
        createGrid();
        this.setSize(width, height);
        AutomataCanvas canvas = new AutomataCanvas(kultainen_x, kultainen_y, height, width);
        getContentPane().add(canvas);
    }
    
    /** This method is called from within the init() method to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents

        setFont(new java.awt.Font("Arial", 0, 10));
    }//GEN-END:initComponents
    public void createGrid(){
        AutomataEngine engine = new AutomataEngine();
        kultainen_x =  engine.laskeKultainen(20, width); //int tarkkuus, int leveys
        kultainen_y =  engine.laskeKultainen(20, height);
    }
   
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
    
}
